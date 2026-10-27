# Rebuild gosu with a currently patched Go toolchain.
# The official PostgreSQL image currently bundles a gosu binary whose Go
# standard library is reported with HIGH/CRITICAL findings by Trivy.
FROM --platform=$BUILDPLATFORM golang:1.27.3-alpine AS gosu-builder

ARG TARGETOS
ARG TARGETARCH

RUN apk add --no-cache git \
    && git init /src \
    && git -C /src remote add origin https://github.com/tianon/gosu.git \
    && git -C /src fetch --depth=1 origin 6456aaa0f3c854d199d0f037f068eb97515b7513 \
    && git -C /src checkout --detach FETCH_HEAD

WORKDIR /src
RUN CGO_ENABLED=0 GOOS=${TARGETOS} GOARCH=${TARGETARCH} \
    go build -trimpath -ldflags="-s -w" -o /gosu . \
    && /gosu --version

FROM postgres:17-alpine
COPY --from=gosu-builder --chmod=0755 /gosu /usr/local/bin/gosu
RUN gosu --version && gosu nobody true
