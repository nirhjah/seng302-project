#!/bin/bash
#
# This project will provide a TUI to choose the tests to run

######################################################################
# define project directory and other relevant directories
######################################################################
PROJECT_DIR=.
TESTS_DIR=${PROJECT_DIR}/src/test/
JAVA_TESTS=${TESTS_DIR}/java/nz/ac/canterbury/seng302/tab/
TEST_FEATURES=${TESTS_DIR}/resources/features/
# integration tests
INTEGRATION_TESTS_DIR=${TEST_FEATURES}/integration/
INTEGRATION_TEST_FILES=""
# unit tests
UNIT_TESTS_DIR=${JAVA_TESTS}/unit/
UNIT_TEST_FILES=""
# End2end tests
END2END_TESTS_DIR=${TEST_FEATURES}/end2end/
END2END_TEST_FILES=""
######################################################################
# packages - used for running a specific test in a folder
######################################################################
SRC_PACKAGE="nz.ac.canterbury.seng302.tab"
# searching for a specific test
UNIT_PACKAGE=${SRC_PACKAGE}".unit"
######################################################################
# user specific variables
TEST_TYPE=""
# idk what to call the next variable -- it is to check if the user wants to run all, one file or one individual test
TEST_SPECIFIER="" # All, Whole-file, Individual

######################################################################

######################################################################
# main header
######################################################################
gum style \
	--foreground 212 --border-foreground 212 --border double \
	--align center --width 50 --margin "1 2" --padding "1 2" \
	'Test Configuration'

# starts the program and waits for it to load
wait_for_program_start() {
	./gradlew bootRun &
	# get the pid of the program so we can kill running it after the tests are done
	program_pid=$!
	while ! nc -z localhost 8080; do
		sleep 1
	done
}

######################################################################
# Asks the user for the type of test
# maybe make it so you can run all ie unit+integration+end2end
######################################################################
prompt_for_test_type() {
	echo '{{ Color "99" "0" "What type of test would you like to run?" }}' | gum format -t template
	TEST_TYPE=$(gum choose --limit 1 Unit Integration End2end)

}

######################################################################
# asks the user how many tests they want to run
######################################################################
prompt_for_single_or_all_test_by_name() {
	# echo "Run all tests, a whole file or an individual test?"
	echo '{{ Color "99" "0" "Run all tests, a whole file or an individual test?" }}' | gum format -t template
	TEST_SPECIFIER=$(gum choose --limit 1 All Whole-file Individual)
}

######################################################################
# if it is all tests then run ./gradlew "test"
######################################################################
run_all_tests() {
	if [[ $TEST_TYPE == "Unit" ]]; then
		cd ${PROJECT_DIR} && ./gradlew test
	else
		cd ${PROJECT_DIR} && ./gradlew "${TEST_TYPE}"
	fi
}

######################################################################
#                            UNIT TESTS                              #
######################################################################

# gets all the filenames in the unit test directory
search_for_filename_unit_test() {
	UNIT_TEST_FILES=$(find $UNIT_TESTS_DIR -name "*.java" | sed 's#.*/##' | sed 's/\.java$//')
	echo "Filter file"
	CHOSEN_FILE=$(echo "$UNIT_TEST_FILES" | gum filter)
	# we need to find the name of the parent directory to be able to run the test(s)
	CHOSEN_FILE_PARENT_DIR=$(find $UNIT_TESTS_DIR -name "$CHOSEN_FILE.java" -print0 | xargs -0 dirname | xargs -0 basename)

}

# find all the tests inside the file
search_individual_unit_tests() {
	# finds the names of all the tests in the chosen file
	FILE_DIR=$UNIT_TESTS_DIR/$CHOSEN_FILE_PARENT_DIR/$CHOSEN_FILE.java
	# the 3 indicated it will search fo a method name up to 3 lines under the @Test
	TEST_NAMES=$(grep -A 3 "@Test" "${FILE_DIR}" | sed -n '/void/p' | sed 's/.*void \(.*\)().*/\1/')
	CHOSEN_TEST=$(echo "$TEST_NAMES" | gum filter)
}

# run the specific test that the user has chosen
run_specific_unit_test() {
	# find chosen test in file
	search_individual_unit_tests
	./gradlew :test --tests "$UNIT_PACKAGE.$CHOSEN_FILE_PARENT_DIR.$CHOSEN_FILE.$CHOSEN_TEST"
}

# currently only runs single unit test file
run_specific_file() {
	./gradlew :test --tests "$UNIT_PACKAGE.$CHOSEN_FILE_PARENT_DIR.$CHOSEN_FILE"

}

######################################################################
#                         INTEGRATION TESTS                          #
######################################################################

# Used to search for an integration test feature to run
search_for_filename_integration_test() {
	INTEGRATION_TEST_FILES=$(find $INTEGRATION_TESTS_DIR -name "*.feature" | sed 's#.*/##' | sed 's/\.feature$//')
	echo "Filter file"
	CHOSEN_FILE=$(echo "$INTEGRATION_TEST_FILES" | gum filter)
}

# finds the names of all the scenarios in the chosen file
search_for_individual_integration_scenario() {
	FILE_DIR=$INTEGRATION_TESTS_DIR/$CHOSEN_FILE.feature
	# gets all lines with the word scenario and removes scenario or scenario outline and remove spaces from front
	TEST_NAMES=$(grep Scenario "${FILE_DIR}" | sed 's/Scenario\(:\| Outline:\)//' | sed 's/^[[:space:]]*//')
	CHOSEN_TEST=$(echo "$TEST_NAMES" | gum filter)

}

run_specific_integration_test() {
	search_for_individual_integration_scenario
	# run the chosen test
	wait_for_program_start
	./gradlew end2end -PcucumberOpts="--tests '*$CHOSEN_TEST*' $INTEGRATION_TESTS_DIR/$CHOSEN_FILE.feature"
	kill $program_pid
}

# runs an individual end2end file
run_specific_integration_file() {
	wait_for_program_start
	./gradlew end2end -PcucumberOpts="$INTEGRATION_TESTS_DIR/$CHOSEN_FILE.feature"
	kill $program_pid
}

######################################################################
#                           END2END TESTS                            #
######################################################################

search_for_filename_end2end_test() {
	END2END_TEST_FILES=$(find $END2END_TESTS_DIR -name "*.feature" | sed 's#.*/##' | sed 's/\.feature$//')
	echo "Filter file"
	CHOSEN_FILE=$(echo "$END2END_TEST_FILES" | gum filter)
	echo "chosen file = $CHOSEN_FILE"
}

# finds the names of all the scenarios in the chosen file
search_for_individual_end2end_scenario() {
	FILE_DIR=$END2END_TESTS_DIR/$CHOSEN_FILE.feature
	# gets all lines with the word scenario and removes scenario or scenario outline and remove spaces from front
	TEST_NAMES=$(grep Scenario "${FILE_DIR}" | sed 's/Scenario\(:\| Outline:\)//' | sed 's/^[[:space:]]*//')
	CHOSEN_TEST=$(echo "$TEST_NAMES" | gum filter)

}

run_specific_end2end_test() {
	search_for_individual_end2end_scenario
	# run the chosen test
	wait_for_program_start
	./gradlew end2end -PcucumberOpts="--tests '*$CHOSEN_TEST*' $END2END_TESTS_DIR/$CHOSEN_FILE.feature"
	kill $program_pid
}

# runs an individual end2end file
run_specific_feature_file() {
	wait_for_program_start
	./gradlew end2end -PcucumberOpts="$END2END_TESTS_DIR/$CHOSEN_FILE.feature"
	kill $program_pid
}

######################################################################
#                           Main Logic                               #
######################################################################
prompt_for_test_type
prompt_for_single_or_all_test_by_name

if [[ $TEST_SPECIFIER == "All" ]]; then
	echo "Running all ${TEST_TYPE} tests"
	run_all_tests
	exit 0
fi

# unit test cases
if [[ $TEST_TYPE == "Unit" ]]; then
	# need the file name for both cases
	search_for_filename_unit_test
	if [[ $TEST_SPECIFIER == "Individual" ]]; then
		run_specific_unit_test
	elif [[ $TEST_SPECIFIER == "Whole-file" ]]; then
		run_specific_file
	fi
	exit 0
fi

# End2End test cases
if [[ $TEST_TYPE == "End2end" ]]; then
	# need the file name for both cases
	search_for_filename_end2end_test
	if [[ $TEST_SPECIFIER == "Individual" ]]; then
		run_specific_end2end_test
	elif [[ $TEST_SPECIFIER == "Whole-file" ]]; then
		run_specific_feature_file
	fi
	exit 0
fi

# integration test cases
if [[ $TEST_TYPE == "Integration" ]]; then
	# need the name of the feature?
	search_for_filename_integration_test
	if [[ $TEST_SPECIFIER == "Individual" ]]; then
		run_specific_integration_test
	elif [[ $TEST_SPECIFIER == "Whole-file" ]]; then
		run_specific_integration_file
	fi
	exit 0
fi
