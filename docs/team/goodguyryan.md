# Ryan Ng's Project Portfolio Page

## Project: FinTrack
FinTrack is a lightweight command-line assistant that helps you keep an eye on day-to-day spending and income. It is designed for users who prefer a fast, keyboard-first workflow without navigating complex spreadsheets.

The following is the link to my code contribution to this project: [RepoSense Link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=goodguyryan&breakdown=true)

Given below are my contributions to the project:

- **New Feature**: Implement the base `Expense` class and `add-expense` feature
    - What it does: The expense class defines what an `Expense` is and consists of while the `add-expense` allows the addition of an `Expense` to a list of `Expenses`.
    - Justification: Core feature of FinTrack as an expense tracker and allows for basic functionality of tracking expenses.
    - Highlights: Is now the base of all expense related features.

- **New Feature**: Implement the `summary-expense` and `summary-income` feature
    - What it does: Gives a basic summary and breakdown of either expenses or income.
    - Justification: This feature improves the product significantly as it gives users an overall view of their finances with just one command, hence, allowing them to plan their finances easier and better.
    - Highlights: This command improves the user experience significantly with a clean view of their finances from one command.

- **New Feature**: Implement the `tips` feature
    - What it does: Gives a money saving tip that is especially helpful for our targeted user.
    - Justification: This feature differentiates our app from being too generic and allows us to especially help our targeted user.
    - Highlights: Is able to give a wide range of useful tip to our targeted users.

- **Enhancements**:
    - Implement `ExpenseCategory` and `ImplementCategory` enums which limits users to a set list of categories, allowing for a easier development of other features such as `budget` whist improving on base features such as `Expenses` and `Income`: [#60](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/60)
    - Wrote tests for full coverage of `Expense`, `Income`, and `TipsStorage` class and additional test for additional coverage for summary feature. [#39](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/39) , [#97](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/97)


- **Documentation**:
    - User Guide:
        - Wrote instructions on how to use the `summary-expense` command.
        - Wrote instructions on how to use the `summary-income` command.
        - Wrote instructions on how to use the `tips` command.
    - Developer Guide
        - Wrote design & implementation details on the `summary` feature (with sequence diagrams).
        - Wrote design & implementation details on the `budget` feature (with sequence diagrams).
        - Wrote product scope for the project which includes target user profile and value proposition.
- **Project Management**:
    - Coordinated team efforts in meeting deadlines and requriements especially for documentation. Responsible for building and deploying jar files during releases.
    - Open and closed issues and reviewed other team members PRs with non-trivial comments.

- **Community**:
    - PRs reviewed (with non-trivial comments): [#80](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/80) , [#75](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/74) , [#74](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/74) , [#62](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/62) , [#41](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/41)