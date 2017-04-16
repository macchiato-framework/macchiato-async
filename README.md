# macchiato/async

Build status: 
[![CircleCI](https://circleci.com/gh/macchiato-framework/macchiato-async.svg?style=svg)](https://circleci.com/gh/macchiato-framework/macchiato-async)

Currently a trivial ClojureScript wrapper for [node-fibers](https://github.com/laverdet/node-fibers). Originally wrote to be used in Macchiato, but can be used standalone.

In the near future we plan to experiment with different approaches. See the [TODO](#TODO) section below.

Current version is `0.0.4-SNAPSHOT` and probably changing regularly. 

## Rationale

We're building Macchiato on node. Everything's async. We need a coherent way of approaching this.

Originally we discussed keeping this as part of macchiato/core, since chances are *every library we use will need it*. Then again, we can use other Macchiato libraries without using the ring-like functionality of `core` - say, database access, which will depend on futures, could be used to build lambdas for AWS.

Keeping it independent allows us this flexibility.

## Usage

See `macchiato.test.async.futures` for sample uses.

## TODO

All the things!  This is just a rough draft and the API might change. 

- [x] Likely renaming the library to macchiato/async, since we may end up doing multiple implementations under the same library.
- [ ] I'm currently looking at async/wait on Node 7. We might want to use that instead of wrapping a custom library.
- [ ] Review [synchronize.js](http://alexeypetrushin.github.io/synchronize/docs/index.html) and consider a ClojureScript port.

Also, once the API settles:

- We have some initial tests for futures, expanding the test cases would be nice.
- More documentation

## Contributing

I welcome pull requests, as long as you're also distributing them under the MIT License.

We are using git-flow. Please submit your pull requests from develop.


# License

Copyright © 2016 Numergent Limited. Distributed under the MIT License.