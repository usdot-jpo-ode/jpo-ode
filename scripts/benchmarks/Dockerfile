FROM python:3.12.5-slim-bookworm

WORKDIR /benchmarks
COPY scripts/benchmarks/requirements.txt /benchmarks/requirements.txt
RUN pip install --no-cache-dir --disable-pip-version-check \
    -r /benchmarks/requirements.txt

ENTRYPOINT ["python", "/benchmarks/decode_benchmark.py"]
