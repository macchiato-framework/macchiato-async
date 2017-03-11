# Change Log

This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

Assume that any upgrade before we hit 0.1.0 is potentially breaking.

## 0.0.4-SNAPSHOT

## 0.0.3

### Breaking changes

- Changed parameter order for `wrap-future`. Previously the full argument signature matched the parameter order from `Future.wrap`, but I think it's better to keep the parameters from the various ClojureScript signatures in a consistent order.  

### Other

- CircleCI integration

## 0.0.2

- **BREAKING**: Renamed `macchiato.futures` -> `macchiato.futures.core`
- Added initial tests.

## 0.0.1

- First version