# Noelle Then's Project Portfolio Page

## Project: FinTrack
FinTrack is a lightweight command-line assistant that helps you keep an eye on day-to-day spending and income. It is designed for users who prefer a fast, keyboard-first workflow without navigating complex spreadsheets. 

The following is the link to my code contribution to this project: [RepoSense Link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=noellethen&breakdown=true)

Given below are my contributions to the project:
- **New Feature**: Added the base Parser class to handle user input.
  - What it does: Relevant methods in this class are called (by `FinTrack`) to transform raw user `String` input into structured validated data objects that are meaningful for the application to work.
  - Justification: This feature improves the product significantly as it is the first line of defense against bad user input, ensuring that the application runs smoothly.
  - Highlights: This enhancement affects commands to be added in the future, which required an in-depth analysis of design alternatives.
- **New Feature**: Added the ability to set budgets.
  - What it does: Allows  the user to set budgets for each expense category, one at a time, and they do not have to set a budget for each category.
  - Justification: This feature improves the product significantly as the user can now plan their finances more accurately and prudently with a budget available for them to keep to.
  - Highlights: This enhancement affects existing commands (such as `add-expense`), and was challenging as it required changes to existing commands. For instance, when a user goes overbudget when adding an expense via `add-expense`, FinTrack will update the user that they have overspent in the output.
- **New Feature**: Added the ability to list budgets.
  - What it does: allows the user to see their budgets set for expense categories with an existing budget. Otherwise, it lets the user know that they have not set a budget for any expense category.
  - Justification: This feature improves the product significantly as the user can have a bird's-eye view of their budgets for relevant categories, allowing them to better plan their finances.
- **New Feature**: Added the ability to delete budgets.
    - What it does: allows the user to delete their budgets set for expense categories. Otherwise, it lets the user know that they have not set a budget for any expense category.
    - Justification: This feature improves the product significantly as the user can delete their outdated budgets to prevent the app from repeatedly sending warnings.
- **Enhancements to existing features**:
  - Wrote additional tests for existing features to increase code coverage: [#106](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/106)
- **Documentation**: 
  - User Guide:
    - Wrote instructions on how to use the `budget` command.
    - Wrote instructions on how to use the `list-budget` command.
    - Wrote instructions on how to use the `delete-budget` command.
  - Developer Guide
    - Wrote design & implementation details on the `FinTrack` class (with sequence diagrams).
    - Wrote design & implementation details on the `Parser` class (with sequence diagrams).
    - Wrote non-functional requirements for the project.
- **Project Management**: 
  - Managed the issue tracker, assigning milestones to relevant pull requests, as well as opening and closing new issues where applicable.
- **Community**:
  - PRs reviewed (with non-trivial comments): [#95](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/95), [#90](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/90), [#81](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/81), [#73](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/73), [#57](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/57), [#46](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/46)