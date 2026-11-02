#!/usr/bin/env python3
"""Generate distinct, constraint-checked UPER corpora from scripts/tests sender examples."""

import argparse
import ast
import hashlib
import json
import re
import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TYPES = {
    "bsm": ("jpo-ode-svcs/src/test/resources/us/dot/its/jpo/ode/udp/bsm/BsmReceiverTest_ValidBSM.txt", None,
            ("BasicSafetyMessage", "coreData", "id"), "hex32"),
    "map": ("scripts/tests/udpsender_map.py", "MESSAGE", ("MapData", "intersections", 0, "id", "id"), "uint16"),
    "spat": ("scripts/tests/udpsender_generic.py", "SPAT_MESSAGE", ("SPAT", "intersections", 0, "id", "id"), "uint16"),
    "tim": ("scripts/tests/udpsender_tim.py", "MESSAGE", ("TravelerInformation", "timeStamp"), "tim_timestamp"),
    "psm": ("jpo-ode-svcs/src/test/resources/us/dot/its/jpo/ode/udp/psm/PsmReceiverTest_ValidPSM.txt", None,
            ("PersonalSafetyMessage", "id"), "hex32"),
    "srm": ("scripts/tests/udpsender_generic.py", "SRM_MESSAGE", ("SignalRequestMessage", "requestor", "id", "stationID"), "uint32"),
    "ssm": ("scripts/tests/udpsender_generic.py", "SSM_MESSAGE", ("SignalStatusMessage", "status", 0, "sigStatus", 0,
          "requester", "id", "stationID"), "uint32"),
    "sdsm": ("scripts/tests/udpsender_sdsm.py", "MESSAGE", ("SensorDataSharingMessage", "sourceID"), "hex32"),
    "rtcm": ("scripts/tests/udpsender_rtcm.py", "MESSAGE", ("RTCMcorrections", "anchorPoint", "long"), "longitude"),
    "rsm": ("scripts/tests/udpsender_rsm.py", "MESSAGE", ("RoadSafetyMessage", "commonContainer", "eventInfo",
          "eventID", "uniqueID"), "hex32"),
}

JAVA_SOURCE = r"""
import j2735ffm.MessageFrameCodec;
import java.nio.file.*;
import java.util.HexFormat;

public class FfmCorpusCodec {
  private static MessageFrameCodec codec(String library) {
    return new MessageFrameCodec(2_097_152, 262_144, 1_024, Path.of(library));
  }
  public static void main(String[] args) throws Exception {
    var codec = codec(args[1]);
    var input = Path.of(args[2]);
    var output = Path.of(args[3]);
    if (args[0].equals("decode")) {
      var hex = Files.readString(input).trim();
      var jer = codec.uperToJer(HexFormat.of().parseHex(hex));
      Files.writeString(output, jer);
    } else if (args[0].equals("encode")) {
      try (var reader = Files.newBufferedReader(input);
           var writer = Files.newBufferedWriter(output)) {
        String jer;
        while ((jer = reader.readLine()) != null) {
          writer.write(HexFormat.of().formatHex(codec.jerToUper(jer)));
          writer.newLine();
        }
      }
    } else {
      throw new IllegalArgumentException("mode must be decode or encode");
    }
  }
}
"""


def set_path(root, path, value):
    parent = root
    for part in path[:-1]:
        parent = parent[part]
    parent[path[-1]] = value


def script_message(script, variable):
    source = ROOT / "scripts/tests" / script
    tree = ast.parse(source.read_text(encoding="utf-8"), filename=str(source))
    for node in tree.body:
        if (isinstance(node, ast.Assign)
                and any(isinstance(target, ast.Name) and target.id == variable
                        for target in node.targets)):
            value = ast.literal_eval(node.value)
            if not isinstance(value, str):
                raise ValueError(f"{source}:{variable} must be a hex string")
            return value
    raise ValueError(f"{source} does not define {variable}")


def seed_message(source, variable):
    path = ROOT / source
    if variable is None:
        return re.sub(r"\s+", "", path.read_text(encoding="ascii"))
    return script_message(path.relative_to(ROOT / "scripts/tests"), variable)


def varying_value(kind, index):
    if kind in ("hex32",):
        return f"{index:08X}"
    if kind == "uint16" and index > 65535:
        raise ValueError("MAP/SPAT intersection identifiers exceed the 16-bit ASN.1 range")
    if kind == "tim_timestamp" and index > 3600000:
        raise ValueError("TIM timestamps exceed the time-of-day range")
    if kind == "longitude":
        return -1800000000 + index
    return index


def slf4j_api(repository):
    api_dir = repository / "org/slf4j/slf4j-api"
    candidates = []
    for jar in api_dir.glob("*/*.jar"):
        if jar.name != f"slf4j-api-{jar.parent.name}.jar":
            continue
        version = tuple(int(part) if part.isdigit() else 0
                        for part in re.split(r"[.-]", jar.parent.name))
        if version and version[0] >= 2:
            candidates.append((version, jar))
    if not candidates:
        raise FileNotFoundError("SLF4J 2.x API is missing from the local Maven repository")
    return max(candidates)[1]


def run_codec(java, classpath, library, mode, source, destination):
    result = subprocess.run(
        [java, "--enable-native-access=ALL-UNNAMED", "-cp", classpath,
         "FfmCorpusCodec", mode, str(library), str(source), str(destination)],
        capture_output=True, text=True)
    if result.returncode:
        raise RuntimeError(
            f"FFMLib {mode} failed for {source}: {result.stderr.strip() or result.stdout.strip()}")
    return result


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output-dir", type=Path,
                        default=ROOT / "scripts/tests/output/linux-multitype-adm4-20260930")
    parser.add_argument("--native-library", type=Path,
                        default=ROOT / "jpo-ode-svcs/target/libs/libasnapplication.so")
    parser.add_argument("--count", type=int, default=50000)
    parser.add_argument("--warmup", type=int, default=1000)
    args = parser.parse_args()
    total = args.count + args.warmup
    if args.count <= 0 or args.warmup < 0:
        parser.error("count must be positive and warmup must be nonnegative")
    if total > 65535:
        parser.error("the default MAP/SPAT corpus field supports at most 65,535 distinct messages")
    args.output_dir = args.output_dir.resolve()
    args.native_library = args.native_library.resolve()
    if args.output_dir.exists() and any(
            (args.output_dir / f"{name}.packets").exists() for name in TYPES):
        parser.error(f"refusing to overwrite existing packet corpora in {args.output_dir}")
    if (args.output_dir / "corpus-manifest.json").exists():
        parser.error(f"refusing to overwrite existing corpus manifest in {args.output_dir}")
    if not args.native_library.is_file():
        parser.error(f"FFMLib native library not found: {args.native_library}")

    repository = Path.home() / ".m2/repository"
    version = re.search(r"<ffmlib\.version>([^<]+)</ffmlib\.version>",
                        (ROOT / "jpo-ode-svcs/pom.xml").read_text()).group(1)
    ffmlib = repository / f"com/neaeraconsulting/j2735-2024-ffm-lib/{version}/j2735-2024-ffm-lib-{version}.jar"
    if not ffmlib.is_file():
        parser.error(f"FFMLib {version} is not resolved in the local Maven repository: {ffmlib}")
    log_api = slf4j_api(repository)
    classpath = f"{ffmlib}:{log_api}"
    args.output_dir.mkdir(parents=True, exist_ok=True)

    manifest = {"codec": f"FFMLib {version}", "warmup_per_type": args.warmup,
                "measured_per_type": args.count, "unique_packets_per_type": total,
        "source": "beta6-decodable J2735 UDP sender examples; BSM and PSM use valid ODE test fixtures because the scripts/tests sender packets fail beta6 decoding; one valid ASN.1 identifier/timestamp varies per packet",
                "types": {}}
    with tempfile.TemporaryDirectory(prefix="ode-multitype-corpus-") as temporary:
        temp = Path(temporary)
        source = temp / "FfmCorpusCodec.java"
        source.write_text(JAVA_SOURCE)
        subprocess.run(["javac", "-cp", classpath, str(source)], check=True)
        runtime_classpath = f"{temp}:{classpath}"

        for name, (source_name, variable, field_path, field_kind) in TYPES.items():
            source_path = ROOT / source_name
            if not source_path.is_file():
                parser.error(f"message example is missing: {source_path}")
            fixture_hex = seed_message(source_name, variable).lower()
            if not fixture_hex or not re.fullmatch(r"[0-9a-f]+", fixture_hex) or len(fixture_hex) % 2:
                parser.error(f"message example is not valid hex: {source_name}::{variable}")

            fixture_input = temp / f"{name}.hex"
            base_jer_file = temp / f"{name}.jer"
            fixture_input.write_text(fixture_hex)
            run_codec("java", runtime_classpath, args.native_library, "decode",
                      fixture_input, base_jer_file)
            base_jer = json.loads(base_jer_file.read_text())
            payload = base_jer["value"]
            if len(payload) != 1:
                parser.error(f"fixture has unexpected MessageFrame payload: {name}")
            field_path = ("value", *field_path)

            request_file = temp / f"{name}.jsonl"
            with request_file.open("w", encoding="utf-8") as requests:
                for index in range(1, total + 1):
                    message = json.loads(json.dumps(base_jer))
                    set_path(message, field_path, varying_value(field_kind, index))
                    requests.write(json.dumps(message, separators=(",", ":")) + "\n")

            corpus = args.output_dir / f"{name}.packets"
            encoded_file = temp / f"{name}.packets"
            run_codec("java", runtime_classpath, args.native_library, "encode",
                      request_file, encoded_file)
            packet_hash = hashlib.sha256()
            unique_count = 0
            with encoded_file.open(encoding="ascii") as encoded:
                with corpus.open("x", encoding="ascii") as output:
                    for line in encoded:
                        packet = line.strip().lower()
                        if not packet or len(packet) % 2 or not re.fullmatch(r"[0-9a-f]+", packet):
                            raise RuntimeError(f"FFMLib emitted invalid UPER for {name}")
                        output.write(packet + "\n")
                        packet_hash.update(bytes.fromhex(packet))
                        packet_hash.update(b"\n")
                        unique_count += 1
            if unique_count != total:
                raise RuntimeError(f"{name} corpus contains {unique_count} packets; expected {total}")
            with corpus.open(encoding="ascii") as generated:
                if len({line.strip() for line in generated}) != total:
                    raise RuntimeError(f"{name} corpus contains duplicate UDP datagrams")
            manifest["types"][name] = {"example": source_name + (f"::{variable}" if variable else ""),
                "varying_field": ".".join(map(str, field_path)), "packets": unique_count,
                "sha256_lines": packet_hash.hexdigest()}
            print(f"{name.upper()}: generated {unique_count:,} unique FFMLib-encoded messages")

    (args.output_dir / "corpus-manifest.json").write_text(json.dumps(manifest, indent=2) + "\n")
    print(args.output_dir)


if __name__ == "__main__":
    main()
