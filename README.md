# Example Modular Android App

## Description

This is an example codebase of an Android app architected using multiple modules. Modules provide a number of benefits:

- Reuse of common components and code across multiple teams/apps
- Ability to manage development and distribution of modules individually
- Easier support for Dynamic Feature modules (i.e. Instant Apps)
- Faster build times for large code bases
- Enforcement of code boundaries
- Better testability by virtue of isolation
- Flexibility to distribute, open source, or publish specific modules

In this example we have a single repository that contains all the different Gradle modules. 
However, *the modules could easily be extracted to separate repos and be maintained and published by different teams as external dependencies*.
The modules are completely sand-boxed from each other, so it does not matter whether they are built from the same codebase or brought in as an AAR dependency.
The choice to contain all modules in here was simply for the convenience of demonstration.

To convert this to multi-team/multi-repo approach you would simply move each module to it's own repo and update each `build.gradle` to pull those modules in as external dependencies instead of local dependencies.
I.E. `implementation project(':common')` would become `implementation sean.k:common{published_dependency_version}`.

### When should you do this?

Modularizing your codebase is a good solution when:

A) You are an organization with multiple teams trying to share code across different apps
B) You have a large monolithic codebase that could benefit from the build time and maintainability improvements modularization offers
C) There are components of your app you would like to extract and publish as a library 

### When shouldn't you do this?

There is build complexity overhead of having separate modules. 
This includes managing transitive dependencies, increased build configuration code, and difficulties in keeping configurations consistent. 

If you are working in a small, relatively simple codebase then you may not stand to gain much from modularization.
Instead, just focus on organizing your code within your single module and utilize packages for encapsulation.

## Code Overview

### Dependency structure

This codebase contains 4 separate modules:

1. `app` -- the top level app module that generates an APK. This contains a screen that can navigate to some of the share screens
2. `account` -- Contains a screen that shows current login and session information
3. `home` -- Contains a basic hello world screen
4. `common` -- The base shared module that contains util class, common UI elements, styles, themes, and login components

The relationship between the modules flows in one direction. This is important to avoid cyclical dependencies.

In a scenario where multiple apps exist that would like to reuse these shared modules, another top level module would be created akin to our `app` module.
Note that this top-level module could be its own codebase entirely (bringing published modules in as external dependencies) or another top level module of this single codebase.

### Shared UI and components

Components from one module can be accessed by any other modules that add it as a dependency. 
This includes custom views as well as fragments and activities. Activities added to the manifest of one module will be automatically added to the manifest of a dependent when those manifests are merged.
This makes it easy to launch or embed shared flows across multiple apps. 

In this example app, the `common` module contains a self-contained `LoginActivity` as well as a custom `LoginBannerView` that shows a contextual greeting and allows the user to either sign in or sign out.
This means each parent module can display login status, read session data, and launch the login flow. 
Changes to the login flow can be made in one place and applied to multiple apps as a dependency. 
Those apps can update to newer versions on their own schedule.

The same is true for non-UI components, such as complicated business logic, util classes, networking code, analytics, logging, etc

### Shared Themes and Styles

Any themes and custom styles defined in one module will be automatically made available in any dependent modules.
In this codebase, the `common` module contains a common theme, definition of branding colors, and typefaces.
This means that any changes to branding can be made in `common` which will automatically apply to dependent modules when those modules bring in the latest version of `common`.

Through this method, themes and styles can easily be shared whether defined as XML or with Compose.

### Automation and Testing

Both UI and Unit/Integration tests can be performed at the module level to test that module in isolation.

*TODO: testing examples in code*

### Deployment

If modules are intended to be shared across multiple code bases, they can be published to a maven repository and brought in as AAR dependencies.
This lets each dependent code base opt into changes and upgrade at their own convenience.

Publishing can be generally configured in the `build.gradle` of a module. *TODO: examples of this*

### Granularity of modules

Take into consideration what should be extracted into separate modules and what should be grouped together. 
In other words, think about how granular your modules should be.  
Modularization can be a good thing, but over-modularization can be detrimental to a project. 
When deciding whether to extract code into its own module, always weigh the benefits against the inherent build and dependency management complexities of an additional module.

As an example, suppose our `common` module contains logging, network code, util classes, login/auth logic, and shared custom views.
Generally, it is simpler to just keep all of these things together. 
You would be introducing more build boilerplate and dependencies by splitting them up, which would get especially complicated if any of those smaller modules depended on each other.

However, as the `common` module evolves it may reach a size where it is too large to be a single module. 
Perhaps build times are very slow, or you have multiple teams working on isolated parts of the `common` module that are running into conflicts. 
For example, perhaps one team is focused on theming and building out custom views, while another is totally focused on login and auth.

The level to which you modularize your project should take all of these factors into consideration. *Avoid modularizing just for the sake of modularizing*