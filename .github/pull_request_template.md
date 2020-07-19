## What is the purpose of the change

XXXXX

## Brief ChangeLog

XXXXX

## Verifying this change

XXXXX

Follow this checklist to help us incorporate your contribution quickly and easily:

- [x] Make sure there is a [GITHUB_issue](https://github.com/LinShunKang/MyPerf4J/issues) field for the change (usually before you start working on it). Trivial changes like typos do not require a GITHUB issue. Your pull request should address just this issue, without pulling in other changes - one PR resolves one issue.
- [ ] Format the pull request title like `[MyPerf4J-XXX] Fix NullPointerException when host is null #XXX`. Each commit in the pull request should have a meaningful subject line and body.
- [ ] Write a pull request description that is detailed enough to understand what the pull request does, how, and why.
- [ ] Write necessary unit-test to verify your logic correction, more mock a little better when cross module dependency exist.
- [ ] Run `mvn clean package -Dmaven.test.skip=false` to make sure unit-test pass.