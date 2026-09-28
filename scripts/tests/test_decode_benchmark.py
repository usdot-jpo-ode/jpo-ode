"""Unit tests for benchmark statistics and its strict acceptance checks."""

import math
import unittest

from decode_benchmark import (classify_latency, complete_window_rates, is_drained_to_watermarks,
                              is_create_time_timestamp, is_group_committed_to_watermarks,
                              latency_windows, MetricSnapshot, QuietPeriod,
                              measured_message_ids, metric_estimates, parse_ffmlib_metrics,
                              parse_outstanding_metrics, parse_stage_metrics, percent,
                              validation_errors)


class DecodeBenchmarkTest(unittest.TestCase):

    def test_percentile_uses_linear_interpolation(self):
        self.assertAlmostEqual(3.85, percent([1, 2, 3, 4], .95))
        self.assertIsNone(percent([], .95))

    def test_p95_threshold_is_strict(self):
        errors = valid_errors(p95=5.0, maximum=5.0)
        self.assertIn("p95_ms 5.000 is not below 5.000 ms", errors)
        self.assertEqual([], valid_errors(p95=4.999, maximum=5.0))

    def test_measured_ids_exclude_warmup_prefix(self):
        self.assertEqual({102, 103, 104}, measured_message_ids(100, 2, 3))

    def test_raw_and_json_correlation_requires_every_expected_id(self):
        errors = valid_errors(expected={1, 2}, raw={1}, output={1, 2})
        self.assertIn("missing 1 raw records", errors)
        self.assertNotIn("missing 1 JSON records", errors)

    def test_duplicates_and_unexpected_quarantine_fail(self):
        errors = valid_errors(duplicate_raw=1, duplicate_json=2, dlt_count=1)
        self.assertIn("found 1 duplicate raw records", errors)
        self.assertIn("found 2 duplicate JSON records", errors)
        self.assertIn("found 1 unexpected quarantine records", errors)

    def test_invalid_rate_timestamps_and_missing_p95_fail(self):
        errors = valid_errors(actual_rate=900, invalid_timestamps=1, p95=None, maximum=5.0)
        self.assertTrue(any("outside +/-5%" in error for error in errors))
        self.assertIn("found 1 invalid timestamps or negative latencies", errors)
        self.assertIn("p95_ms is missing or invalid", errors)

    def test_materially_unsustained_send_gap_fails(self):
        errors = valid_errors(max_send_gap=1000.0, max_send_gap_limit=1000.0)
        self.assertIn(
            "maximum inter-packet send gap 1000.0 ms is not below 1000.0 ms", errors)

    def test_application_group_offsets_must_reach_raw_watermarks(self):
        starts = {("raw", 0): 10}
        ends = {("raw", 0): 12}
        self.assertFalse(is_group_committed_to_watermarks({("raw", 0): 11}, "raw", ends, starts))
        self.assertTrue(is_group_committed_to_watermarks({("raw", 0): 12}, "raw", ends, starts))

    def test_10_second_rates_use_only_complete_windows(self):
        sends = [index / 1000 for index in range(20000)] + [20.5]
        self.assertEqual([1000.0, 1000.0], complete_window_rates(sends, 1000))

    def test_latency_windows_exclude_warmup_and_report_complete_windows(self):
        send_times = {message_id: {"monotonic_s": float(message_id)}
                      for message_id in range(21)}
        latencies = {message_id: float(message_id) for message_id in range(21)}

        windows = latency_windows(send_times, latencies, 1, 20, 1)

        self.assertEqual(2, len(windows))
        self.assertEqual(10, windows[0]["paired"])
        self.assertAlmostEqual(9.55, windows[0]["p95_ms"])
        self.assertEqual(10, windows[1]["paired"])
        self.assertAlmostEqual(19.55, windows[1]["p95_ms"])

    def test_stage_histograms_are_grouped_by_stage_and_metric(self):
        text = "\n".join((
            'ode_ffmlib_decode_stage_seconds_bucket{stage="native",le="0.001"} 80',
            'ode_ffmlib_decode_stage_seconds_bucket{host="ode2",stage="native",le="0.001"} 20',
            'ode_ffmlib_decode_stage_seconds_bucket{stage="native",le="+Inf"} 100',
            'ode_ffmlib_decode_stage_seconds_bucket{host="ode2",stage="native",le="+Inf"} 30',
            'ode_ffmlib_raw_record_age_seconds_bucket{le="0.005"} 95'))

        stages = parse_stage_metrics(text)

        self.assertEqual({0.001: 100.0, float("inf"): 130.0}, stages["native"])
        self.assertEqual({0.005: 95.0}, stages["ode_ffmlib_raw_record_age"])

    def test_raw_and_offset_metrics_are_parsed_for_drain(self):
        text = "\n".join((
            "ode_ffmlib_raw_publication_in_flight 0.0",
            "ode_ffmlib_offset_commit_in_flight 2.0"))
        self.assertEqual((0.0, 2.0), parse_outstanding_metrics(text))

    def test_nonfinite_percentile_samples_are_rejected(self):
        with self.assertRaises(ValueError):
            percent([1.0, math.inf], .95)

    def test_submillisecond_negative_timestamp_is_retained_as_quantization(self):
        self.assertEqual((-0.5, "quantization_negative"), classify_latency(100.0, 100.5))
        self.assertEqual((None, "invalid"), classify_latency(99.0, 100.5))

    def test_only_create_time_timestamps_are_accepted(self):
        self.assertTrue(is_create_time_timestamp(1))
        self.assertFalse(is_create_time_timestamp(0))
        self.assertFalse(is_create_time_timestamp(2))
        self.assertFalse(is_create_time_timestamp(None))

    def test_labeled_ffmlib_metrics_report_confirmation_and_pending_count(self):
        text = '\n'.join((
            'ode_ffmlib_output_confirmation_seconds_bucket'
            '{enabled="true",topic="topic.OdeBsmJson",le="0.005"} 8',
            'ode_ffmlib_output_confirmation_seconds_bucket'
            '{enabled="false",topic="topic.OdeBsmJson",le="0.005"} 2',
            'ode_ffmlib_output_confirmation_seconds_bucket'
            '{enabled="true",topic="topic.OtherJson",le="0.005"} 50',
            'ode_ffmlib_output_in_flight{enabled="true",host="ode"} 0.0'))

        buckets, pending = parse_ffmlib_metrics(text, "topic.OdeBsmJson")

        self.assertEqual({0.005: 10.0}, buckets)
        self.assertEqual(0.0, pending)

    def test_quiet_period_restarts_when_health_or_pending_work_changes(self):
        quiet = QuietPeriod(1.0)
        drained = True
        clear = MetricSnapshot(pending_output=0, pending_raw=0, pending_commits=0)
        pending = MetricSnapshot(pending_output=1, pending_raw=0, pending_commits=0)

        self.assertFalse(quiet.observe(0.0, drained, True, clear, True))
        self.assertFalse(quiet.observe(0.8, drained, False, clear, True))
        self.assertFalse(quiet.observe(1.5, drained, True, pending, True))
        self.assertFalse(quiet.observe(2.0, drained, True, clear, True))
        self.assertTrue(quiet.observe(3.0, drained, True, clear, True))

    def test_external_mode_keeps_summary_metrics_empty_without_ffm_gauges(self):
        before, after = MetricSnapshot(), MetricSnapshot()
        estimates, stage = metric_estimates(before, after, ffm_mode=False)

        self.assertEqual({"confirmed_p50_estimate_ms": None,
                          "confirmed_p95_estimate_ms": None,
                          "confirmed_p99_estimate_ms": None}, estimates)
        self.assertEqual({}, stage)
        self.assertEqual([], valid_errors(dlt_count=1, ffm_checks=False))

    def test_drained_check_ignores_untouched_partitions(self):
        starts = {("topic.OdeBsmJson", 0): 10, ("topic.DLT", 0): 0}
        ends = {("topic.OdeBsmJson", 0): 12, ("topic.DLT", 0): 0}
        positions = [FakePosition("topic.OdeBsmJson", 0, 12),
                     FakePosition("topic.DLT", 0, -1001)]

        self.assertTrue(is_drained_to_watermarks(positions, ends, starts))

    def test_drained_check_rejects_unconsumed_new_records(self):
        starts = {("topic.OdeBsmJson", 0): 10}
        ends = {("topic.OdeBsmJson", 0): 12}
        positions = [FakePosition("topic.OdeBsmJson", 0, 11)]

        self.assertFalse(is_drained_to_watermarks(positions, ends, starts))


def valid_errors(expected={1}, raw=None, output=None, duplicate_raw=0, duplicate_json=0,
                 dlt_count=0, invalid_timestamps=0, actual_rate=1000, max_send_gap=1.0,
                 max_send_gap_limit=1000.0, p95=4.0, maximum=5.0, ffm_checks=True,
                 health_ok=True):
    raw = set(expected) if raw is None else set(raw)
    output = set(expected) if output is None else set(output)
    return validation_errors(expected, raw, output, {item: 1.0 for item in expected},
        duplicate_raw, duplicate_json, 0, dlt_count, invalid_timestamps, actual_rate, 1000,
        [1000.0], max_send_gap, max_send_gap_limit, p95, maximum, 0.0, 0.0, 0.0,
        True, True, health_ok=health_ok, ffm_checks=ffm_checks)


class FakePosition:
    def __init__(self, topic, partition, offset):
        self.topic = topic
        self.partition = partition
        self.offset = offset


if __name__ == "__main__":
    unittest.main()
