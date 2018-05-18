copy asn1_codec\asn1c_combined\J2735_201603DA.ASN .

rem Run the following commands to reset existing branch
git reset --hard
git submodule foreach --recursive git reset --hard

rem Pull from branch ode-769.
git checkout %1
git pull origin %1

rem The next command wipes out all of the submodules and re initializes them. 
git submodule deinit -f .
git submodule update --recursive --init

rem Restore the J2735 ASN file
copy .\J2735_201603DA.ASN asn1_codec\asn1c_combined


