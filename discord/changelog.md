## [0.5.0-SNAPSHOT]

### Added

- Added webhook APIs.
- Added Apache/Tika (core) to validate images for avatar builder.
- Added some Guild API endpoints. Most of them are redundant anyways except for few in particular.
- Added emoji endpoints.

### Changes

- Refactored Sharding.
  - Previously `chatty-discord` would not attempt to resume shards when disconnecting from them as well as send "Identify"
calls every time it reconnected.
  - Also DiscordSession now properly disconnects when receiving OP 7 request code.
- RateLimitInterceptor is now deprecated and will be removed in 1.0.0. Implement an AbstractRateLimiter instead.
    - Added GuildLimiter, AccountLimiter and ChannelLimiter implementations. They're used  
- Refactored some endpoints
    - Message creation endpoints are now not general case and instead just contain 
    a single field for each type: builder, embed or attachment.
- `DiscordController.getUser` now does a network call if it can't find user in cache. 

## [0.4.3-SNAPSHOT]

### Fixed

- Fixed an issue where member would be modified incorrectly

## [0.4.2-SNAPSHOT]

### Fixed

- Fixed an issue where Discord Connection Controller would ignore member changes.

## [0.4.1-SNAPSHOT]

### Changed

- Discord Connection controller now handles ready events again.

## [0.4.0-SNAPSHOT]

### Added

- Discord session wrapper which permits resuming, reconnection
and provides multiple shard support.

### Removed

- Gateway related events from `DiscordConnectionController`

## [0.3.0-UNSTABLE]

### Breaking changes

- Depends on `chatty-websocket:4.0.1-SNAPSHOT`

## [0.2.0-UNSTABLE]

### Breaking changes:

- `chatty-discord` now depends on `chatty-websocket:3.0.1`
- Removed helper method `writeResponse` in `DiscordController`

### Changes

- `DiscordController` now depends on `AsyncController`

## Unreleased

## [0.1.2]

### Changes

- Now `chatty-discord` depends on `chatty-websocket:2.2.0`

### Added

- Added `RateLimitInterceptor`. It's supposed to handle request rate limits for applications implenting this
framework. You may set `RateLimitInterceptor.shouldWait` to true, if instead of throwing, you want to wait for
request to go through instead. 
- Added remaining API calls for channel resource.


## [0.1.1] 2017-08-18

### Changes

- Instead of depending on raw project, `chatty-discord` now depends on `chatty-websocket:1.0.1`

## [0.1] 2017-08-17

### Added

- A lot of events that might come through the session handler.
- Added `DiscordRouter`, which is specifically meant for discord implementations.
- Added `DiscordMiddleware`, which is specifically meant for discord implementations
- Added `DiscordRoute` which is specifically meant for discord implementations
- Added `DiscordRouteBuilder` which is specifically meant for discord implementations.
- Added compressed message handler, which indirectly depends on `DiscordAdapter`.
- Added connection controller, which handles requests from websocket.
- Added Retrofit as a dependency to make it easier to manage HTTP endpoints for Discord API.
- Added OKHTTP as a side dependency for Retrofit to make it easier to handle requests.

### Changes

- `WebSocketEndpoint` is now in `chatty.v3.discord.main` package

### Removed

- Removed dependencies on deprecated APIs