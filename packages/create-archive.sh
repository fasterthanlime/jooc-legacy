#!/bin/bash
DIR=$(dirname $0)
DATE=$(date -u +"%Y%m%d")
pushd $DIR
cd ..
    VERSION=$(cat utils/version.txt | awk '{print $1}')
    git archive --format=tar --prefix=ooc-${VERSION}-git${DATE}/ HEAD | gzip > packages/ooc-${VERSION}-git${DATE}.tar.gz
popd

