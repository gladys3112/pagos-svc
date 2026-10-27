FROM postgres:17-alpine

# The upstream image bundles gosu, whose embedded Go standard library triggers
# HIGH/CRITICAL Trivy findings. su-exec provides the same user-switch-and-exec
# behavior used by the PostgreSQL entrypoint, with a much smaller Alpine binary.
RUN apk add --no-cache su-exec \
    && rm /usr/local/bin/gosu \
    && ln -s /sbin/su-exec /usr/local/bin/gosu \
    && gosu postgres true
