FROM debian:stable-slim

RUN apt-get update && apt-get install -y \
  sudo \
  passwd \
  util-linux \
  man-db \
  nano \
  openssl \
  ca-certificates \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /root
CMD ["/bin/bash"]