# User Account App

### App In Progress

<img src="/videos/user_account_feature.gif" width="300">

## Overview
Utilizing Android Interface Definition Language (AIDL), this app is build with a client and service functionality that runs on two separate Android Processes. The mobile app allows users to register / authenticate and manage their personal account.

The service is built as a data persistence layer that allows syncing with backend data stores. The UI layer is the client that binds to this service and calls functions from the AIDL contract that is implemented by the service.

* Data Persistence Layer - Repository Pattern (Retrofit API calls & Room Caching)
* Presentation Layer - MVVM
* Key Technologies - Retrofit, RxJava, Dagger, AIDL

## Known Issues
- Had trouble with using relative endpoint paths in retrofit interface so temporarily using full path in the interface.
