# Contributing

## License Agreement

By contributing changes to this repository, you agree to license your contributions under the [GNU Affero General Public License](https://www.gnu.org/licenses/agpl-3.0.html). This ensures your contributions have the same license as the project and that the community is free to use your contributions. You also assert that you are the original author of the work that you are contributing unless otherwise stated.

## Submitting an Issue

We use the [issue tracker on GitHub](https://github.com/McPringle/alfons/issues) associated with this project to track bugs and features (i.e., issues). We very much appreciate the time and effort you take to report an issue.

Before submitting an issue, make sure it hasn't already been submitted by using the [search feature](https://github.com/McPringle/alfons/issues). Please be sure to check closed issues as well as the issue may have been recently fixed.

If you've determined that your issue has not already been reported, please follow these guidelines when submitting an issue:

- Use an actionable title that identifies the behavior you want, such as `Allow attributes to be defined per list item`.
- Add a description that explains your use case and why this behavior will help you achieve your goal. Also include any details that may help reproduce the bug, including your Java version, *Alfons* version, and operating system.
- An ideal bug report would also include a pull request with at least one failing spec. However, we recognize that not everyone who uses *Alfons* is a Java programmer, or even a programmer. So we do not expect you to include a pull request with your issue.

Condescending or disparaging remarks have no place in this issue tracker and will result in your issue being rejected. You can be critical, but keep it positive and constructive. Stick with actionable language that describes what you would like the software to do.

Be mindful of the fact that this project is maintained by volunteers and built on a foundation of trust. Please respect the work of those who have volunteered their time and effort to develop this project, and we will respect the time and effort you have taken to file an issue.

## Submitting a Pull Request

1. [Fork the repository](https://help.github.com/articles/fork-a-repo).
2. [Create a topic branch](https://docs.github.com/de/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-and-deleting-branches-within-your-repository) (preferably using the pattern `issue-XYZ`, where `XYZ` is the issue number).
3. Add tests for your unimplemented feature or bug fix.
4. Run `./mvnw verify` to run the tests and check the source code for possible errors. If your tests and the checks pass, return to step 3.
5. Implement your feature or bug fix.
6. Run `./mvnw verify` to run the tests and check the source code for possible errors. If your tests or the checks fail, return to step 5.
7. Add documentation for your feature or bug fix.
8. If your changes are not 100% documented, go back to step 7.
9. Add, commit, and push your changes.
10. [Submit a pull request](https://help.github.com/articles/using-pull-requests).

For ideas about how to use pull requests, see the post http://blog.quickpeople.co.uk/2013/07/10/useful-github-patterns[Useful GitHub Patterns].
