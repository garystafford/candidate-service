#!/bin/bash

# Drop all candidates and POST new candidates to CosmosDB, through API Gateway

url="http://api.voter-demo.com:8080/candidate/candidates"

candidates=(
  '{"firstName":"Mitt","lastName":"Romney","politicalParty":"Republican Party","election":"2012 Presidential Election"}'
  '{"firstName":"Rocky","lastName":"Anderson","politicalParty":"Justice Party","election":"2012 Presidential Election"}'
  '{"firstName":"Jill","lastName":"Stein","politicalParty":"Green Party","election":"2012 Presidential Election"}'
  '{"firstName":"Gary","lastName":"Johnson","politicalParty":"Libertarian Party","election":"2012 Presidential Election"}'
  '{"firstName":"Virgil","lastName":"Goode","politicalParty":"Constitution Party","election":"2012 Presidential Election"}'
  '{"firstName":"Barack","lastName":"Obama","politicalParty":"Democratic Party","election":"2012 Presidential Election"}'
  '{"firstName":"Donald","lastName":"Trump","politicalParty":"Republican Party","election":"2016 Presidential Election"}'
  '{"firstName":"Chris","lastName":"Keniston","politicalParty":"Veterans Party","election":"2016 Presidential Election"}'
  '{"firstName":"Jill","lastName":"Stein","politicalParty":"Green Party","election":"2016 Presidential Election"}'
  '{"firstName":"Gary","lastName":"Johnson","politicalParty":"Libertarian Party","election":"2016 Presidential Election"}'
  '{"firstName":"Darrell","lastName":"Castle","politicalParty":"Constitution Party","election":"2016 Presidential Election"}'
  '{"firstName":"Hillary","lastName":"Clinton","politicalParty":"Democratic Party","election":"2016 Presidential Election"}'
)

echo "Dropping all existing candidate documents..."
curl --request POST \
 --url $url/drop \

echo ""

for candidate in "${candidates[@]}"
do
  echo "POSTing $candidate"
  curl --request POST \
   --url $url \
   --header 'content-type: application/json' \
   --data "$candidate"
done
