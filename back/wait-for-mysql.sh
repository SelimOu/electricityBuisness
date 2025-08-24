#!/bin/bash
set -e
host="$1"
port="$2"
shift 2
cmd="$@"

until mysql -h "$host" -P "$port" -uroot -proot -e 'SELECT 1' >/dev/null 2>&1; do
  echo "Waiting for MySQL at $host:$port..."
  sleep 1
done

echo "MySQL is up"
exec $cmd
