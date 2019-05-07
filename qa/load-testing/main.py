from argparse import ArgumentParser
import os
import requests

DOCKER_HOST_IP=os.getenv('DOCKER_HOST_IP')
assert DOCKER_HOST_IP != None, "Failed to get DOCKER_HOST_IP from environment variable"
UPLOAD_URL = "http://%s:8080/upload/bsmlog" % DOCKER_HOST_IP
UPLOAD_FILE = "../../data/bsmLogDuringEvent.gz"

def upload_file():
    with open(UPLOAD_FILE, 'rb') as file:
        return requests.post(UPLOAD_URL, files={'name':'file', 'file':file}, timeout=2)

def main():
    parser = ArgumentParser()
    parser.add_argument("-c", "--count", dest="count", help="Number of times to repeat upload.", metavar="COUNT", required=True)
    args = parser.parse_args()

    for i in range(int(args.count)):
        upload_response = upload_file()
        print("Upload response received: %s %s" % (upload_response.status_code, upload_response.text))

if __name__ == "__main__":
    main()
