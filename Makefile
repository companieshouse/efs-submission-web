artifact_name       := efs-submission-web
version             := unversioned

.PHONY: clean
clean:
	mvn clean
	rm -f ./$(artifact_name).jar
	rm -f ./$(artifact_name)-*.zip
	rm -rf ./build-*
	rm -rf ./build.log-*

.PHONY: test-unit
test-unit: coverage

.PHONY: verify
verify: coverage

.PHONY: unit-coverage
unit-coverage: clean
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false jacoco:prepare-agent test jacoco:report

.PHONY: coverage
coverage: clean
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false jacoco:prepare-agent test \
	jacoco:prepare-agent-integration verify \
	jacoco:report jacoco:report-integration

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	$(info Packaging version: $(version))
	@test -s ./$(artifact_name).jar || { echo "ERROR: Service JAR not found"; exit 1; }
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./start.sh $(tmpdir)
	cp ./routes.yaml $(tmpdir)
	cp ./$(artifact_name).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: build
build:
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -Dskip.unit.tests=true
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

.PHONY: dist
dist: test-unit

.PHONY: sonar
sonar:	security-check
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar -P sonar-pr-analysis

.PHONY: security-check
security-check:
	mvn compile org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=11 -DassemblyAnalyzerEnabled=false

