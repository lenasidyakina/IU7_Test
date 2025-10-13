./gradlew :BL:test :DB:test
rm -r build/reports/allure-report
./gradlew allureReport
./gradlew clean