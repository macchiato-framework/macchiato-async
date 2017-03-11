# macchiato/futures

Build status: 
[![CircleCI](https://circleci.com/gh/macchiato-framework/macchiato-futures.svg?style=svg)](https://circleci.com/gh/macchiato-framework/macchiato-futures)

Trivial ClojureScript wrapper for [node-fibers](https://github.com/laverdet/node-fibers). Originally wrote to be used in Macchiato, but can be used standalone.

Current version is `0.0.3-SNAPSHOT` and probably changing regularly. 

Using git-flow. Please submit your pull requests from develop.

## Rationale

We're building Macchiato on node. Everything's async. We need a coherent way of approaching this.

Originally we discussed keeping this as part of macchiato/core, since chances are *every library we use will need it*. Then again, we can use other Macchiato libraries without using the ring-like functionality of `core` - say, database access, which will depend on futures, could be used to build lambdas for AWS.

Keeping it independent allows us this flexibility.

## Usage

See `macchiato.test.futures.core` for sample uses.


## TODO

All the things!  This is just a rough draft and the API might change. 

- We have some initial tests, expanding the test cases would be nice.
- More documentation



# License

Copyright © 2016 Numergent Limited. Distributed under the MIT License.