
### Phases

1. **Phase** A user is able to provide a Buildpacks specific DSL that specifies a builder, a path, and image name which results in an image created as \<image name>.
2. **Phase** A user should be able to fully use [defined DSL](https://hackmd.io/NIfdbyRNRcGpBtaNjIyvrQ?view) expressions.

3. **Phase** What can be added in addition to DSL ? For example: colorful outputs, connected [java-buildpack-client](https://github.com/snowdrop/java-buildpack-client) outputs to plugin...

### Related Work

**Issues:**
1. https://github.com/snowdrop/java-buildpack-client/issues/12 (Closed)
2. https://github.com/snowdrop/java-buildpack-client/issues/13 (Closed)
3. https://github.com/snowdrop/java-buildpack-client/issues/15 (Open)

**Pull Request:**

1. https://github.com/snowdrop/java-buildpack-client/pull/14 (Under review)


### Conclusion

Terminal commands were already available in Jenkins. So this allowed you to use the pack command. However, when you want to write a pipeline script, it didn't give a chance to have a system that works compatible with jenkins. In addition, it was a non-recommended use. 

Jenkins pipeline DSL Buildpacks plugin allows you to use buildpacks compatible with Jenkins. The base system is currently available. What needs to be done is to fix the bugs and add new features.
