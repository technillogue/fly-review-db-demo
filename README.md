## A demo for setting up Heroku-style review apps with Fly and GHA.

When you open a PR in this repo, it's automatically served at review-{branch name}.fly.dev. Pushing more commits redeploys. Closing or merging the PR destroys the app.

You can test the review app for this branch with `grpcurl  review-feat-test-endpoint.fly.dev:443 demo.v1.UserService.Foo`.

Generating repo secrets: 
- `FLY_API_TOKEN`: [Install flyctl and login](https://fly.io/), then copy `fly auth token`
- `REVIEW_APP_SECRETS`: The secrets for app in the same format as `fly secrets set`, e.g. `MY_SALT=iodized NUCLEAR_ACCESS_CODE=hunter2`
- `REVIEW_DB_NAME`: Create a postgres cluster with `fly postgres create` (or as part of a `fly launch` prompt), then but the app name here. This cluster will be shared by the review apps, each one connecting to a different database. 

This particular demo uses Kotlin to run a simple gRPC server with Exposed and Liquibase for databases access and management. This repo shows off using Liquibase to generate migration SQL and feeling it to `fly postgres connect` for setting up lightweight databases. For a simpler version of review apps that doesn't use Postgres, see <https://github.com/technillogue/fly-review-demo>. If your app image can do migrations, you can use [release_command](https://fly.io/docs/reference/configuration/#release_command).

You can use [grpCURL](https://github.com/fullstorydev/grpcurl) to test your deployment.

`grpcurl review-demo.fly.dev:443 list demo.v1.UserService`

To run locally:

`./gradlew run`

To test:

`./gradlew test`

Format:

`./gradlew spotlessApply`
