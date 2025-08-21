Simple Session service:
    Scala / Cats-effect / circe

State preserved in-Memory using Ref data structure: https://typelevel.org/cats-effect/docs/std/ref

TODO:
  ~~* Fix issue compile time warnings~~
  ~~* Add logging~~
    ~~* Add Tests~~
    ~~* Enhance logging: capture value while evaluating~~
    * Implement simple queue: item in(String) and item out

Play with:
    ** Design use session service funcs using MapRef
        + add TTL logic | background logic   
    ** Implement Graceful shutdown
    ** Auth
    ** Middleware
    ** Streaming
    ** Integration Testing
    ** More complex CE3.x data structures like: 
            [ Clock | Timer | Semaphore | MVar2 | Deferred | ] 

TODO: Other enhancements
    * Add config in the FP way
    * Add Redis service docker definition
    * Add Redis service and integrate it as an APP resource

        