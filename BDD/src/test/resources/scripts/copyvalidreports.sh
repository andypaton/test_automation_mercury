# Copy valid json files to a new folder prior to creating the cucumber reports
echo "Start: Copy cucumber json report files"
mkdir ${WORKSPACE}/BDD/target/validresults/
echo "copying files from /BDD/target/results/ "
find ${WORKSPACE}/BDD/target/results/ -name \*.json -type f -size +0 -print0 | xargs -0 -I % cp % ${WORKSPACE}/BDD/target/validresults/
echo "copying files from /BDD/target/cucumber-parallel/ "
find ${WORKSPACE}/BDD/target/cucumber-parallel/ -name \*.json -type f -size +0 -print0 | xargs -0 -I % cp % ${WORKSPACE}/BDD/target/validresults/
echo "End: Copy cucumber json report files"