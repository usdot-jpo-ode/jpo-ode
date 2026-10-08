# Response to Ivan’s FFM PR review

Reviewed PR [#602](https://github.com/usdot-jpo-ode/jpo-ode/pull/602) and PR [#603](https://github.com/usdot-jpo-ode/jpo-ode/pull/603), including inline comments and both routing review summaries from `iyourshaw` on October 8, 2026.

## Accepted changes

- **Use beta6 ([comment](https://github.com/usdot-jpo-ode/jpo-ode/pull/602#discussion_r4223337858)).** Agreed. The existing local foundation update already pins the Java dependency and both native artifacts to `3.0.0-beta6`; routing inherits that update. The adapter uses the shared codec and library-managed buffers.
- **Packaged-jar native loading ([comment](https://github.com/usdot-jpo-ode/jpo-ode/pull/602#discussion_r4223496565)).** Agreed. A nested-jar code source must not be passed to `Path.of`. Restrict build-output discovery to a `file:` URI pointing to a directory, keep the remaining fallback candidates, and set the Docker runtime native-library directory explicitly to `/home/libs`. Add regression coverage for packaged-jar URIs and ordinary build output.
- **TIM native error messages ([comment](https://github.com/usdot-jpo-ode/jpo-ode/pull/603#discussion_r4223602839)).** Agreed that the invented error prefixes never matched the library. Recognize actual native decoding and constraint failure messages, including nested `ConvertException` causes, to return HTTP 400 for invalid request data.
- **Obsolete compact-buffer comment ([comment](https://github.com/usdot-jpo-ode/jpo-ode/pull/603#discussion_r4223654873)).** Agreed. Remove the stale line from `sample.env`; beta6 manages reusable buffers.
- **ASD documentation ([review](https://github.com/usdot-jpo-ode/jpo-ode/pull/603#pullrequestreview-5462228997)).** Agreed. TIM MessageFrames and the subsequent AdvisorySituationData encoding for SDW deposits both run in process in FFM mode. Correct the README and UserGuide. PPM continues on its external path.
- **Signed UDP messages ([review](https://github.com/usdot-jpo-ode/jpo-ode/pull/603#pullrequestreview-5462228997)).** Agreed. `parseFfmRecord` already extracts MessageFrame UPER using the external route’s stripping rules. Rejecting that payload merely because the original packet has a signed header prevents otherwise decodable traffic. Allow the stripped raw-topic payload through, preserve existing ASN.1 and security metadata, and test decoded publication confirmation before acknowledgement. This performs payload decoding, not signature verification. A missing MessageFrame start flag still causes parsing failure and quarantine.

## Where I would keep the current behavior

### Some native failures should still return HTTP 500

The supplied message list is useful, but the messages do not all mean the request is invalid. An undersized native output buffer or failure to encode the configured output format is a conversion/configuration failure, so it should remain HTTP 500. Mapping every `ConvertException` to 400 would misidentify those service failures as caller errors. Decoding and constraint-check failures return 400; native encoding, output-size/buffer, and unknown failures retain 500. Tests cover both groups.

### Keep durable raw topics and confirmed output before committing input

In response to the [overall design review](https://github.com/usdot-jpo-ode/jpo-ode/pull/603#pullrequestreview-5462641918), I agree that two publications, wrapping/parsing, confirmation waits, and frequent commits have a cost. I would retain the current delivery behavior in this PR:

- **Keep `acks=all` and idempotence.** `acks=1` weakens replication acknowledgement and is incompatible with the current idempotent producer configuration. A successful acknowledgement followed by loss of the leader before replication can lose the decoded output after its raw offset was committed. `acks=all` still depends on replication factor and broker/topic minimum in-sync replicas; it does not by itself make a one-replica deployment resilient. See the [Kafka producer configuration](https://kafka.apache.org/41/configuration/producer-configs/#acks) and [idempotence requirements](https://kafka.apache.org/41/configuration/producer-configs/#enable.idempotence).
- **Keep raw-topic ingestion before decoding.** It gives accepted Kafka records a replay point if decoding or decoded publication fails, preserves the existing raw-topic consumer contract, and supports switching back to external decoding using the same committed offsets. UDP itself can drop packets before Kafka accepts them; these guarantees apply after ingestion. Making raw topics optional or decoding only on the UDP receiver would change those contracts and expose receiver throughput to decode delays.
- **Keep the current acknowledgement sequence for this change.** Poll batching or timer commits can preserve at-least-once delivery if offsets advance only past contiguous records with confirmed JSON or quarantine output. They are reasonable follow-up optimizations, but need dedicated failure, partial-poll, shutdown, replay, and latency validation. A timer alone must never commit unconfirmed output. The current configuration already uses asynchronous offset commits by default (`ODE_FFM_SYNC_COMMITS=false`), though it still requests commits per acknowledged record.

Small linger and compression are compatible with these guarantees. Both already have deployment controls: `ODE_FFM_PRODUCER_LINGER_MS` and `ODE_FFM_PRODUCER_COMPRESSION_TYPE`. Document those controls and keep the latency-oriented defaults (`0`, `none`) until representative measurements justify a default change. `linger.ms=0` does not prove that every record becomes a separate broker request: concurrently available records can still share a batch. See [Kafka batching behavior](https://kafka.apache.org/41/configuration/producer-configs/#batch.size). I would benchmark a small linger with `lz4` or `zstd` before claiming a particular saving or changing the defaults.

The suggested cheaper designs are valid for deployments that explicitly accept the changed delivery and raw-topic contracts. That tradeoff has not been selected for these PRs, so I would not introduce it while fixing this review.

## Validation

- Foundation loader regression suite: 4 tests passed.
- Combined Java 25 reactor selection: 114 tests passed, with zero failures, errors, or skips. Command: `mvn -pl jpo-ode-svcs -am '-Dtest=Ffmlib*Test,TimDepositControllerTest,TimAsdFfmRoutingTest,RawEncodedJsonServiceFfmTest,SerializationContractTest' -Dsurefire.failIfNoSpecifiedTests=false -Dffmlib.smoke.required=true test`.
- The required native smoke suite executed all three cases; native fixture coverage also decoded the synthetic signed wrapper’s extracted BSM using the real library. This is header-stripping regression coverage, not validation of a cryptographic signature or a captured signed packet.
- Independent final review found no material correctness or regression issues. `git diff --check` passed.
- Service Checkstyle passed at the configured error threshold (zero error-level violations). Existing warning-level findings remain in the service module.
- The packaged Spring Boot jar/container startup was not rerun; the loader regression explicitly exercises `jar:` and `jar:nested:` code-source URIs. Throughput/latency benchmarks were not rerun for these review fixes.
