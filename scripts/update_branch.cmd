@echo off

IF "%1"=="" (
    echo usage: update_branch.sh branch_name
	goto end
)

rem Backup user provided source or configuration files used by submodules
copy asn1_codec\asn1c_combined\J2735_201603DA.ASN .

rem Run the following commands to reset existing branch
git reset --hard
git submodule foreach --recursive git reset --hard

rem Pull from the desired branch
git checkout %1
git pull origin %1

rem The next command wipes out all of the submodules and re-initializes them. 
git submodule deinit -f .
git submodule update --recursive --init

rem Restore user provided source or configuration files used by submodules
copy .\J2735_201603DA.ASN asn1_codec\asn1c_combined

:end
pause
