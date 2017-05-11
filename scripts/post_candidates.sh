#!/bin/sh

# Post 2016 Presidential Election candidates
# Requires HTTPie
# Requires all services are running

set -e

http POST http://localhost:8097/candidate/candidates \
  firstName='Donald' \
  lastName='Trump' \
  politicalParty='Republican Party' \
  election='2016 Presidential Election'

http POST http://localhost:8097/candidate/candidates \
  firstName='Chris' \
  lastName='Keniston' \
  politicalParty='Veterans Party' \
  election='2016 Presidential Election'

http POST http://localhost:8097/candidate/candidates \
  firstName='Jill' \
  lastName='Stein' \
  politicalParty='Green Party' \
  election='2016 Presidential Election'

http POST http://localhost:8097/candidate/candidates \
  firstName='Gary' \
  lastName='Johnson' \
  politicalParty='Libertarian Party' \
  election='2016 Presidential Election'

http POST http://localhost:8097/candidate/candidates \
  firstName='Darrell' \
  lastName='Castle' \
  politicalParty='Constitution Party' \
  election='2016 Presidential Election'

http POST http://localhost:8097/candidate/candidates \
  firstName='Hillary' \
  lastName='Clinton' \
  politicalParty='Democratic Party' \
  election='2016 Presidential Election'

echo ""
echo "Script completed..."
