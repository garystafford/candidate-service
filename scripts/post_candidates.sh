#!/bin/sh

# Post new candidates
# Requires HTTPie
# Requires all services are running

set -e

HOST=${1:-localhost}
TEST_CYCLES=${2:-25}
URL="http://${HOST}:8097"
ELECTION="2016 Presidential Election"

echo "POSTing new candidates..."

TIME1=$(date +%s)
for i in $(seq ${TEST_CYCLES})
do
  TIMEA=($(date +%s%N)/1000000)
  http POST ${URL}/candidate/candidates \
    firstName='Donald' \
    lastName='Trump' \
    politicalParty='Republican Party' \
    election="${ELECTION}" \
    --headers > /dev/null

  http POST ${URL}/candidate/candidates \
    firstName='Chris' \
    lastName='Keniston' \
    politicalParty='Veterans Party' \
    election="${ELECTION}" \
    --headers > /dev/null

  http POST ${URL}/candidate/candidates \
    firstName='Jill' \
    lastName='Stein' \
    politicalParty='Green Party' \
    election="${ELECTION}" \
    --headers > /dev/null

  http POST ${URL}/candidate/candidates \
    firstName='Gary' \
    lastName='Johnson' \
    politicalParty='Libertarian Party' \
    election="${ELECTION}" \
    --headers > /dev/null

  http POST ${URL}/candidate/candidates \
    firstName='Darrell' \
    lastName='Castle' \
    politicalParty='Constitution Party' \
    election="${ELECTION}" \
    --headers > /dev/null

  http POST ${URL}/candidate/candidates \
    firstName='Hillary' \
    lastName='Clinton' \
    politicalParty='Democratic Party' \
    election="${ELECTION}" \
    --headers > /dev/null
  TIMEB=($(date +%s%N)/1000000)
  TIMEC=`expr ${TIMEA} - ${TIMEB}`
  echo "${TIMEC} milliseconds for HTTP POST requests"
done
TIME2=$(date +%s)

TIME3=`expr ${TIME2} - ${TIME1}`
TESTS=`expr ${TEST_CYCLES} \* 6`

echo ""
echo "${TIME3} seconds for ${TEST_CYCLES} test cycles, or ${TESTS} HTTP POST requests"
echo ""
echo "Script completed..."
