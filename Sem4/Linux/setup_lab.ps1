docker build -t linux-lab .
docker run --rm -it -v "${PWD}:/lab_files" linux-lab