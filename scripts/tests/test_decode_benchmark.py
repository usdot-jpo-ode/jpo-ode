"""Unit tests for benchmark statistics and its strict acceptance checks."""

import math
import unittest

from decode_benchmark import (is_drained_to_watermarks, measured_message_ids, parse_ffmlib_metrics,
                              percent, validation_errors)


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

    def test_nonfinite_percentile_samples_are_rejected(self):
        with self.assertRaises(ValueError):
            percent([1.0, math.inf], .95)

    def test_labeled_ffmlib_metrics_report_confirmation_and_pending_count(self):
        text = '\n'.join((
            'ode_ffmlib_output_confirmation_seconds_bucket'
            '{enabled="true",topic="topic.OdeBsmJson",le="0.005"} 8',
            'ode_ffmlib_output_confirmation_seconds_bucket'
            '{enabled="true",topic="topic.OtherJson",le="0.005"} 50',
            'ode_ffmlib_output_in_flight{enabled="true",host="ode"} 0.0'))

        buckets, pending = parse_ffmlib_metrics(text, "topic.OdeBsmJson")

        self.assertEqual({0.005: 8.0}, buckets)
        self.assertEqual(0.0, pending)

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
                 max_send_gap_limit=1000.0, p95=4.0, maximum=5.0):
    raw = set(expected) if raw is None else set(raw)
    output = set(expected) if output is None else set(output)
    return validation_errors(expected, raw, output, {item: 1.0 for item in expected},
        duplicate_raw, duplicate_json, 0, dlt_count, invalid_timestamps, actual_rate, 1000,
        max_send_gap, max_send_gap_limit, p95, maximum, 0.0, True)


class FakePosition:
    def __init__(self, topic, partition, offset):
        self.topic = topic
        self.partition = partition
        self.offset = offset


if __name__ == "__main__":
    unittest.main()
