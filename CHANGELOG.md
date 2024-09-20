# [0.7.0](https://github.com/scc-digitalhub/digitalhub-core/compare/0.7.0-beta10...0.7.0) (2024-09-20)


### Bug Fixes

* python build should set WORKDIR for user instructions ([b270412](https://github.com/scc-digitalhub/digitalhub-core/commit/b2704121262784161b587c6b1f1c69854d2f1609))



# [0.7.0-beta10](https://github.com/scc-digitalhub/digitalhub-core/compare/0.7.0-beta9...0.7.0-beta10) (2024-09-19)


### Features

* add locking to run manager to fix fsm concurrency + add error messages to status ([216d5b2](https://github.com/scc-digitalhub/digitalhub-core/commit/216d5b2ac37f7d2ed905fc9a01c7b0d90006083b))



# [0.7.0-beta9](https://github.com/scc-digitalhub/digitalhub-core/compare/0.7.0-beta8-r4...0.7.0-beta9) (2024-09-18)


### Bug Fixes

* fix python install of requirements.txt ([d8252ae](https://github.com/scc-digitalhub/digitalhub-core/commit/d8252ae43678dae7f20a645b73157ded78a5cc3e))
* fix seriazable definition to avoid loop + fix array def ([6b03c9a](https://github.com/scc-digitalhub/digitalhub-core/commit/6b03c9ac709734c8bce28f76afe69ec344670625))


### Features

* mlserve runtimes add validation to image and path ([523b204](https://github.com/scc-digitalhub/digitalhub-core/commit/523b204c31a85dd4bc9ede492f10d1979a77ecae))
* python job schedule support + schedule validation via regex as crontab ([41b531a](https://github.com/scc-digitalhub/digitalhub-core/commit/41b531a753234a4c34391adac1b1d625f29a218f))



# [0.7.0-beta8](https://github.com/scc-digitalhub/digitalhub-core/compare/0.7.0-beta7r2...0.7.0-beta8) (2024-09-16)


### Bug Fixes

* build sub path on download only if it differs from path ([81566f8](https://github.com/scc-digitalhub/digitalhub-core/commit/81566f8754e49f11258a85c6b236e9ef18bec474))
* clean fileInfo and fix bug in httpStore ([e17053e](https://github.com/scc-digitalhub/digitalhub-core/commit/e17053edc30d4fa86cc0a854b4f7445d8c7af706))
* fix secret removal not working due to patch issue ([63f76a9](https://github.com/scc-digitalhub/digitalhub-core/commit/63f76a9a50b8531c105d4acb64e641d74cf0ce0d))
* fix store key building ([50ad12d](https://github.com/scc-digitalhub/digitalhub-core/commit/50ad12df5cf772e0e8bd69c032ba25579796ba3c))
* handle log collection errors in loop to avoid stopping on first error ([a9e9c25](https://github.com/scc-digitalhub/digitalhub-core/commit/a9e9c25d6a584b0451fde385e0df6d798a23ba30))
* keep k8s status when there is no updates ([97e2229](https://github.com/scc-digitalhub/digitalhub-core/commit/97e2229706dd60c9e84b8ed134e36bc6708effbe))
* remove source path modification for ml serving ([66ed314](https://github.com/scc-digitalhub/digitalhub-core/commit/66ed31450a683e5f17a366c66e60d7c6d316fdd9))
* use latest tag for python runtimes ([8995114](https://github.com/scc-digitalhub/digitalhub-core/commit/8995114bc170007409a93b0b114758ca598d90a7))


### Features

* add deps install for mlflow ([c79db7c](https://github.com/scc-digitalhub/digitalhub-core/commit/c79db7cfb34d6a4c48cf85b118a68c05352458ff))
* add transitions log to run status ([d673785](https://github.com/scc-digitalhub/digitalhub-core/commit/d673785c11e74bca3948f0216872280ef600bb77))
* hide secret and configMaps details from run status ([50a628b](https://github.com/scc-digitalhub/digitalhub-core/commit/50a628bf8a05ee6cc0e92a4d68a0e19b0480b12c))
* monitor restartCounts for deployments to check for errors ([d56de95](https://github.com/scc-digitalhub/digitalhub-core/commit/d56de9565a6915c44638c9936d18a6e108e6a47e))



# [0.7.0-beta7r2](https://github.com/scc-digitalhub/digitalhub-core/compare/0.7.0-beta6...0.7.0-beta7r2) (2024-09-12)


### Bug Fixes

* fix kaniko init volume double definition ([e4fadea](https://github.com/scc-digitalhub/digitalhub-core/commit/e4fadeaf887fafd63fcf9c91ccfc59023804a0fe))
* remove double slash from upload url built path ([5eddbc8](https://github.com/scc-digitalhub/digitalhub-core/commit/5eddbc8dca2fbc22b6b84f276d75e55bed25777f))


### Features

* zip+s3 store ([cd14de3](https://github.com/scc-digitalhub/digitalhub-core/commit/cd14de347b2b5c62a122048a39a7ee6db73526e9))



# [0.7.0-beta6](https://github.com/scc-digitalhub/digitalhub-core/compare/0.7.0-beta5...0.7.0-beta6) (2024-09-06)


### Bug Fixes

* fix service url building ([b370ea9](https://github.com/scc-digitalhub/digitalhub-core/commit/b370ea9d1036f3c81dd82c30ce4737541ff73400))
* schema generation for serializable as ref to fix gui ([ae6be88](https://github.com/scc-digitalhub/digitalhub-core/commit/ae6be8894c6e668c4a5489bda21c570c963cea54))


### Features

* expose fake node for profiles descriptions ([f5bec00](https://github.com/scc-digitalhub/digitalhub-core/commit/f5bec00a47bbf6528380357587c0770b0ad773da))
* json schema custom generator for serializable fields + proxy building for specs before generation ([c5e1664](https://github.com/scc-digitalhub/digitalhub-core/commit/c5e166498d12471fe3d18103195ccc356d98de0f))
* support templates for k8s objects via composite profiles ([663d643](https://github.com/scc-digitalhub/digitalhub-core/commit/663d643c379c5f0c6e4b1597e2a5634cf0d1ec2c))
* transform schema utils into factory + add k8s schema module + enable proxy + build custom spec for templates/profiles ([a8dd58a](https://github.com/scc-digitalhub/digitalhub-core/commit/a8dd58a1aad29950262caba3230450b3cc62d023))
* update run specs to exclude embedded function from schema generation ([69982c3](https://github.com/scc-digitalhub/digitalhub-core/commit/69982c3ee4c2040e904a52b3ffb887e0f452b61a))
* use resources definition from template when provided over user provided ([49c02eb](https://github.com/scc-digitalhub/digitalhub-core/commit/49c02eb52ce285f73023ac294da897b7879aa88c))



# [0.7.0-beta5](https://github.com/scc-digitalhub/digitalhub-core/compare/0.7.0-beta4-1...0.7.0-beta5) (2024-08-30)



# [0.7.0-beta4](https://github.com/scc-digitalhub/digitalhub-core/compare/0.7.0-beta3...0.7.0-beta4) (2024-08-28)



# [0.7.0-beta3](https://github.com/scc-digitalhub/digitalhub-core/compare/0.7.0-beta2...0.7.0-beta3) (2024-08-23)


### Bug Fixes

* add scanning for subpackages to include authorization ([3fb3dbe](https://github.com/scc-digitalhub/digitalhub-core/commit/3fb3dbe431743e202d2659ed177f8baec7302890))
* enforce name as read-only for entities ([c047743](https://github.com/scc-digitalhub/digitalhub-core/commit/c047743fb4032bd112a510c7077a8bb6e61c78dc))
* fix wrong auth used for refresh token consumption ([71a60df](https://github.com/scc-digitalhub/digitalhub-core/commit/71a60df915697b692dd5a6cc6e78f02123bdcf0a))


### Features

* authorization aware service for projects + helper auth to inject roles for project owners ([030fe1b](https://github.com/scc-digitalhub/digitalhub-core/commit/030fe1b1e8ce7f226dc024e730c00703745165b3))



# [0.7.0-beta2](https://github.com/scc-digitalhub/digitalhub-core/compare/0.7.0-beta1...0.7.0-beta2) (2024-08-22)


### Bug Fixes

* fix anonymous auth for token endpoint ([becb7bd](https://github.com/scc-digitalhub/digitalhub-core/commit/becb7bd28eac61104c3459b55bb5959b2914250e))
* revert changed application properties and apply new config ([bc13096](https://github.com/scc-digitalhub/digitalhub-core/commit/bc13096a21387abfbf4c667e59989dc12467bfd2))
* revert changed jwt configuration to correct default ([3c97c06](https://github.com/scc-digitalhub/digitalhub-core/commit/3c97c061139cb0f6177967b6a408a88b89e2e3c0))


### Features

* add exchange token flow between client and (internal/external) jwt auth ([51a24be](https://github.com/scc-digitalhub/digitalhub-core/commit/51a24beb0d10d326a8a66cfdb72bf5ee4cb0f099))
* implement auth server + add refresh + add endpoints + refactoring ([db5782b](https://github.com/scc-digitalhub/digitalhub-core/commit/db5782b1b78bb870e26ffb827732ce5bb425923c))



# [0.6.0](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta13...0.6.0) (2024-07-31)


### Bug Fixes

* fix build issues ([4ecec34](https://github.com/scc-digitalhub/digitalhub-core/commit/4ecec3467967be99bca1da3d00d459dfdb2408ae))
* fix k8s gpu and volumes usage ([ab1981b](https://github.com/scc-digitalhub/digitalhub-core/commit/ab1981b28481f1079d140b31da689e4bbfa63c1d))
* fix kaniko framework usage of secret volume mounts ([62c23b6](https://github.com/scc-digitalhub/digitalhub-core/commit/62c23b68e7255c91bc6adeb2ae1cb9ed1d80cfcf))



# [0.6.0-beta13](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta12...0.6.0-beta13) (2024-07-22)


### Bug Fixes

* fix k8s names sanitization, shorten generated names + disable fix for throw error ([fae02ad](https://github.com/scc-digitalhub/digitalhub-core/commit/fae02ad92b3d23c1fdae1f8461877f61fcb318b1))
* fix templates reading for base k8s framework ([55d4774](https://github.com/scc-digitalhub/digitalhub-core/commit/55d47749123eb57f3d0a0d9f3a20badca4af2a68))
* runtimes should start without k8s ([3cd8046](https://github.com/scc-digitalhub/digitalhub-core/commit/3cd804648fcc285c447c15d64d79b3aba900637f))


### Features

* add label support to k8s templats ([6cfc7fd](https://github.com/scc-digitalhub/digitalhub-core/commit/6cfc7fd8122a86430f87f9da1b0aba2b37939add))
* add security config and default resources to k8s ([6670cc7](https://github.com/scc-digitalhub/digitalhub-core/commit/6670cc7e607bb422949b4554d1e8f92838d5a285))
* k8s improve observability ([8d8e3e7](https://github.com/scc-digitalhub/digitalhub-core/commit/8d8e3e711c97452e9ffc11ba9824ec8b8282fec3))
* k8s make image pull policy configurable ([c526fdd](https://github.com/scc-digitalhub/digitalhub-core/commit/c526fdd48f9aa44813c2332338426fd4606b3c17))
* k8s profiles as templates ([8295d68](https://github.com/scc-digitalhub/digitalhub-core/commit/8295d6868b0df4a488048718b35143f9f39e506c))
* update k8s configurability and logging ([2f28f9e](https://github.com/scc-digitalhub/digitalhub-core/commit/2f28f9e2db37ae37356bbacdacbbd0e2cb1f7281))



# [0.6.0-beta12](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta11...0.6.0-beta12) (2024-07-19)


### Bug Fixes

* fix image pull policy wrongly specified for k8s ([91c3c8f](https://github.com/scc-digitalhub/digitalhub-core/commit/91c3c8f38229cd11de4c9a936f1f39e76d671b2c))
* fix wrong type in runnables written by runtimes ([0d3d950](https://github.com/scc-digitalhub/digitalhub-core/commit/0d3d9501b0865eba460cabd9f8c07178aaccb639))


### Features

* add custom entrypoint for python-runtime ([d7f40d0](https://github.com/scc-digitalhub/digitalhub-core/commit/d7f40d0e3d30cbf91984987ce99aec5b199306ef))
* add function name label to python and container runnables ([6372ac8](https://github.com/scc-digitalhub/digitalhub-core/commit/6372ac826e9e51c3b3249d7ce62684953d2f67e6))
* clean up k8s details in run after deletion ([8929233](https://github.com/scc-digitalhub/digitalhub-core/commit/8929233e651f8863787e369556d6e5d7f2832038))



# [0.6.0-beta11](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta10...0.6.0-beta11) (2024-07-17)


### Bug Fixes

* fix issue with deleting secret values from existing secrets ([a9e44b9](https://github.com/scc-digitalhub/digitalhub-core/commit/a9e44b9b6c6b22bb314223cb74d5dcb141d4d630))
* fix issue with s3 presigner not using path style with custom endpoints ([166e921](https://github.com/scc-digitalhub/digitalhub-core/commit/166e9215c0f59acda2c383175391e13444fd80e6))
* fix removal of orphaned pods for k8s jobs via propagaition policy ([4f4169b](https://github.com/scc-digitalhub/digitalhub-core/commit/4f4169bf16239fab4a7bc26de5ad72e084d20af9))


### Features

* add k8s function label to container runtime runnables ([895895b](https://github.com/scc-digitalhub/digitalhub-core/commit/895895bb9bf7c6c38d3ed16ee11b98602e0f4c6f))
* add namespaced labels to k8s resources ([af26f64](https://github.com/scc-digitalhub/digitalhub-core/commit/af26f64a4979dd9f4abb0404075fa9fd70294025))
* add prev entity to update events ([debe27f](https://github.com/scc-digitalhub/digitalhub-core/commit/debe27f6b12f8d02d63e8fec519598962217d488))



# [0.6.0-beta10](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta9...0.6.0-beta10) (2024-07-12)


### Features

* add service to k8s run status ([2b054b9](https://github.com/scc-digitalhub/digitalhub-core/commit/2b054b98d358904e9d524aa704e55567cc5272b1))
* build container supports source ([22f40db](https://github.com/scc-digitalhub/digitalhub-core/commit/22f40db868e27a43561368b89d8bb773072ee776))



# [0.6.0-beta9](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta8...0.6.0-beta9) (2024-07-12)


### Bug Fixes

* fix incorrect usage of k8deployment framework for serving ([5adc24d](https://github.com/scc-digitalhub/digitalhub-core/commit/5adc24d43d949baf0635b647792a7d47ada78909))
* schema controller should be accessible to all users ([5012805](https://github.com/scc-digitalhub/digitalhub-core/commit/5012805c667d835315e7e33709bcb408601c2745))



# [0.6.0-beta8](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta7...0.6.0-beta8) (2024-07-11)


### Bug Fixes

* add secrets to serving ([43dc2c5](https://github.com/scc-digitalhub/digitalhub-core/commit/43dc2c5ce31e104fd8c7639570cbf800daedb22a))
* enforce sanitization on names generated for k8s and images ([159e5c2](https://github.com/scc-digitalhub/digitalhub-core/commit/159e5c247dfe08ab9533d95f9c4d9456e367228a))



# [0.6.0-beta7](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta6...0.6.0-beta7) (2024-07-11)


### Bug Fixes

* fix k8s init config map and cleanup ([1414aaf](https://github.com/scc-digitalhub/digitalhub-core/commit/1414aafb229e98835211529caa024eab71b8828a))
* remove notNull annotation from python serve: task should have all fields optional ([0ec534e](https://github.com/scc-digitalhub/digitalhub-core/commit/0ec534e91ebe7676f655f9ae7e28f964013ae257))



# [0.6.0-beta6](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta5...0.6.0-beta6) (2024-07-10)


### Bug Fixes

* fix issue with event publisher not distinguishing between runnable listeners ([7d5492d](https://github.com/scc-digitalhub/digitalhub-core/commit/7d5492da9e5f8118f906ec7a56cfeb1286baa0bb))


### Features

* update python spec to default serviceType to nodePort ([f56d8d4](https://github.com/scc-digitalhub/digitalhub-core/commit/f56d8d4a0a39e5c5081a2fecdde323e2b3c3991b))



# [0.6.0-beta4](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta3...0.6.0-beta4) (2024-07-08)



# [0.6.0-beta3](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta2...0.6.0-beta3) (2024-07-04)


### Bug Fixes

* fix incorrect usage of urlEncoder for base64 ([0957532](https://github.com/scc-digitalhub/digitalhub-core/commit/0957532d28bd59251f1cabb31a20a9acea0e49cf))
* fix issue with patching k8s secrets ([61651c4](https://github.com/scc-digitalhub/digitalhub-core/commit/61651c4fe7c0b07f71c9e7f25b8d6b62ed0a20c1))
* fix unauthorized response from project controller with auth disabled ([39f8ef0](https://github.com/scc-digitalhub/digitalhub-core/commit/39f8ef0f5f79927e0e1f6466f7d0a14d5500b379))
* fix wrong state management on delete ([b7c924a](https://github.com/scc-digitalhub/digitalhub-core/commit/b7c924a1912b3de2f61b9acad24e55811e2d12b7))


### Features

* add init_function to python runtime ([30bf8cf](https://github.com/scc-digitalhub/digitalhub-core/commit/30bf8cfcb1ab909dbb2b5edf9d77ee1bebc76e02))
* cleanup k8s listeners and fix errors ([0324ea5](https://github.com/scc-digitalhub/digitalhub-core/commit/0324ea5cdc05b4d6569e66c98b9ecfaa5688857e))
* container runtime from base runtime ([34542bd](https://github.com/scc-digitalhub/digitalhub-core/commit/34542bd099dde4d59993dbfc45387b64de8d59f3))
* dbt runtime from base runtime ([7ea4f8e](https://github.com/scc-digitalhub/digitalhub-core/commit/7ea4f8ee4916ed6ee333756046cfd4146bb7448e))
* kfp runtime from base runtime ([c640230](https://github.com/scc-digitalhub/digitalhub-core/commit/c6402307cc6d6eba3de22f9df3c832f2004f2b26))
* mlrun runtime from base runtime ([1a0ce5a](https://github.com/scc-digitalhub/digitalhub-core/commit/1a0ce5a462a044b256fb0f3e4584fe11a2801853))
* nefertem runtime from base runtime ([befe3a1](https://github.com/scc-digitalhub/digitalhub-core/commit/befe3a1d405b4bd550afbd92207b2a453ef683e5))
* python runtime from base runtime ([e0c765c](https://github.com/scc-digitalhub/digitalhub-core/commit/e0c765ca1151edcba74a176641cd3ca9f9ca9989))
* refactor runtimes to leverage base impl ([4dac16f](https://github.com/scc-digitalhub/digitalhub-core/commit/4dac16f9398ce1a8fbaa04c07f0d87048c74e38d))
* refactor specs usage ([84ab23f](https://github.com/scc-digitalhub/digitalhub-core/commit/84ab23f7ddb72a502f4163a71075993e380e10d8))
* set default value in schema for single value enums ([ff9474a](https://github.com/scc-digitalhub/digitalhub-core/commit/ff9474a69f9097d080c099f1fefb8a98266ec3b5))
* use yaml for kdp workflow field ([83d54d7](https://github.com/scc-digitalhub/digitalhub-core/commit/83d54d722bcbfb6d90acf88233090435451a8274))



# [0.6.0-beta2](https://github.com/scc-digitalhub/digitalhub-core/compare/0.6.0-beta1...0.6.0-beta2) (2024-06-24)


### Bug Fixes

* fix python build issues with requirements ([d39e06a](https://github.com/scc-digitalhub/digitalhub-core/commit/d39e06ae077f80e6be72dea01d1ee938d3d551fb))



# [0.6.0-beta1](https://github.com/scc-digitalhub/digitalhub-core/compare/0.5.0-beta8...0.6.0-beta1) (2024-06-20)


### Bug Fixes

* align python builders logic ([073ce75](https://github.com/scc-digitalhub/digitalhub-core/commit/073ce75726564f2e3b2dd46340e45240875e730b))
* clear edited files ([3a87962](https://github.com/scc-digitalhub/digitalhub-core/commit/3a8796206646f605ebb9d027e9d4480267e51afa))
* clear modified runtimes ([e3782ec](https://github.com/scc-digitalhub/digitalhub-core/commit/e3782ecfcfe992299a5e6ff9742a1140bf24fc9f))
* disable cors support by default ([0b7ed7e](https://github.com/scc-digitalhub/digitalhub-core/commit/0b7ed7ecff69de5e763113ec71d7fec1500dd4f7))
* fix build-tool image pinning to latest ([b0c5e05](https://github.com/scc-digitalhub/digitalhub-core/commit/b0c5e0563408728133197b1411ada525ac8de02a))
* fix issues with building python images + update config ([52156d9](https://github.com/scc-digitalhub/digitalhub-core/commit/52156d998f3941447d9096d10b417523c6fc63b4))
* fix rsync error including hidden files in builder-tool ([caa391f](https://github.com/scc-digitalhub/digitalhub-core/commit/caa391f68ea290169269886b14ce6f05ac5afac5))
* fix wrongly modified container spec ([a8c064a](https://github.com/scc-digitalhub/digitalhub-core/commit/a8c064a199622b3c0c825ea65a4060230ccbecec))
* name audit metadata with snake_case ([f6fb81d](https://github.com/scc-digitalhub/digitalhub-core/commit/f6fb81de2126400873afe0eee643f499937d5efd))
* properly set condition on s3 store ([e3abd76](https://github.com/scc-digitalhub/digitalhub-core/commit/e3abd7695e13a02ada9be2a1350d94fd58753797))
* resolve symlink copy for build-tool ([df23b58](https://github.com/scc-digitalhub/digitalhub-core/commit/df23b587930d8a691ec7e9f16c74d92f188685c1))


### Features

* add headers for api version ([3f5b8eb](https://github.com/scc-digitalhub/digitalhub-core/commit/3f5b8ebc204671b02e5afa7ea403b5ae1a6cb873))
* cleanup redundant python builders ([ae727f7](https://github.com/scc-digitalhub/digitalhub-core/commit/ae727f7ead7a2ce274145fce07e991495a4a2376))
* files proxy initial support for s3/http ([0803999](https://github.com/scc-digitalhub/digitalhub-core/commit/08039994b72fbe5336c4bd4332cb5c40ba883241))



# [0.5.0-beta8](https://github.com/scc-digitalhub/digitalhub-core/compare/0.5.0-beta7...0.5.0-beta8) (2024-06-10)


### Bug Fixes

* update run specs for input/outputs defs ([5b21e99](https://github.com/scc-digitalhub/digitalhub-core/commit/5b21e990609a4369801ae21222b47d83f4e7c8e5))



# [0.5.0-beta7](https://github.com/scc-digitalhub/digitalhub-core/compare/0.5.0-beta6...0.5.0-beta7) (2024-06-06)


### Features

* add k8s runtime/priority classes ([b643c9f](https://github.com/scc-digitalhub/digitalhub-core/commit/b643c9fa51afa252c15bb10c7bf41d002173f8a3))
* run context filter for id like ([9511136](https://github.com/scc-digitalhub/digitalhub-core/commit/9511136f8dbd74669462005c725ac1ef4d329d1f))



# [0.5.0-beta6](https://github.com/scc-digitalhub/digitalhub-core/compare/0.5.0-beta5...0.5.0-beta6) (2024-06-05)


### Features

* python serve task ([a5a9ee9](https://github.com/scc-digitalhub/digitalhub-core/commit/a5a9ee9abd34d6a0e5696d08c7497bf50811e2f4))
* remove runs after finals state (completed+error) via delete + cleanups ([cb4cf32](https://github.com/scc-digitalhub/digitalhub-core/commit/cb4cf322775d7a3403e0604b62fcd9669506d7e9))



# [0.5.0-beta4](https://github.com/scc-digitalhub/digitalhub-core/compare/0.5.0-beta3...0.5.0-beta4) (2024-06-04)


### Bug Fixes

* handle local path in source for python runtime ([0c166b9](https://github.com/scc-digitalhub/digitalhub-core/commit/0c166b979c497d0c1e5b75417c1c27dbabf7e017))


### Features

* add input/outputs to python run ([2caa488](https://github.com/scc-digitalhub/digitalhub-core/commit/2caa4886d6e6ab888736a0b4cc11f63db58ee6c4))



# [0.5.0-beta3](https://github.com/scc-digitalhub/digitalhub-core/compare/0.5.0-beta2...0.5.0-beta3) (2024-05-31)


### Bug Fixes

* fix kaniko building contextSource map as array instead of strings ([4e6fe41](https://github.com/scc-digitalhub/digitalhub-core/commit/4e6fe4170d13989fd3b3eaa09f9df0952735b85a))


### Features

* add openmetadata integration as custom meta obj ([263801c](https://github.com/scc-digitalhub/digitalhub-core/commit/263801c141a685357e12695b35ee399af886a980))
* python runtime job execution ([0593154](https://github.com/scc-digitalhub/digitalhub-core/commit/05931541d3e9d4e4aa545822095d23c2adc0b8bd))
* update specs and add labels ([0390fad](https://github.com/scc-digitalhub/digitalhub-core/commit/0390fad356f3c5cbe6cccff229809dae4c3897ac))



# [0.5.0-beta2](https://github.com/scc-digitalhub/digitalhub-core/compare/0.5.0-beta1...0.5.0-beta2) (2024-05-29)


### Bug Fixes

* fix schema generation for records ([65f2826](https://github.com/scc-digitalhub/digitalhub-core/commit/65f2826f464d2810a164a775f476a9034c2145d0))


### Features

* add title+desc to schemas derived for specs via annotation scan ([d3b27de](https://github.com/scc-digitalhub/digitalhub-core/commit/d3b27de1323f142ff034c384141533af4306ab99))
* auto-generate title and desc in schema for fields + unwrap run specs ([0978f0e](https://github.com/scc-digitalhub/digitalhub-core/commit/0978f0eb94f7c9966e4a2b9e82a56d6bd7bfd057))
* unwrap container run specs + tweak json schema generation ([8d954c8](https://github.com/scc-digitalhub/digitalhub-core/commit/8d954c80ad23ef9b6b84cc481d7a70dcf7f9727f))
* update k8s internal logging ([77b4c10](https://github.com/scc-digitalhub/digitalhub-core/commit/77b4c1002a17feca3f8195765356cbcf4e6d3b3f))



# [0.5.0-beta1](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0...0.5.0-beta1) (2024-05-20)


### Features

* k8s metrics collection ([462b11f](https://github.com/scc-digitalhub/digitalhub-core/commit/462b11fa05f5ad2bc2f382df5886cbf6a3dc00fe))
* log collection and proper refactoring of monitors ([31b4b35](https://github.com/scc-digitalhub/digitalhub-core/commit/31b4b353ba70d472c68f0c7038a01c618ea6894f))
* model service align ([d572dcd](https://github.com/scc-digitalhub/digitalhub-core/commit/d572dcd66a761568c83c0d9a095cf75d7679dc86))
* remove timestamp from logs ([3a50713](https://github.com/scc-digitalhub/digitalhub-core/commit/3a507137397470ecc611ba0f17a33f9d0dea938e))
* support models for ml ([aa1ba15](https://github.com/scc-digitalhub/digitalhub-core/commit/aa1ba15dbd87af3f9b02ef8c6e00dd6f2adaab12))
* update log handling, collect all containers ([759d536](https://github.com/scc-digitalhub/digitalhub-core/commit/759d5362e335b03cbf50da8a742b124329b1cdab))



# [0.4.0](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta13...0.4.0) (2024-05-14)


### Bug Fixes

* always use (dhcore) for managed-by label ([55ecad0](https://github.com/scc-digitalhub/digitalhub-core/commit/55ecad01934d841396c99356dc46324847d64488))



# [0.4.0-beta13](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta12...0.4.0-beta13) (2024-05-14)


### Bug Fixes

* fix issues with run lifecycle in k8s + bump kubernetes client dep ([0ce18b9](https://github.com/scc-digitalhub/digitalhub-core/commit/0ce18b9df99f849ea578687add2659d5a46a3a2d))
* fix name in pom + fix nullpointer in k8serve ([f42b439](https://github.com/scc-digitalhub/digitalhub-core/commit/f42b439acb43eaea999358db10168edd164646d3))



# [0.4.0-beta12](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta11...0.4.0-beta12) (2024-05-10)


### Bug Fixes

* fix k8s labels ([b774509](https://github.com/scc-digitalhub/digitalhub-core/commit/b7745096358b50ae8c10f85c8e4da037ad21d365))
* project name and must must be equals ([5c4b20f](https://github.com/scc-digitalhub/digitalhub-core/commit/5c4b20f71272433ec9c97ab5c0a2e056527f66b8))


### Features

* use release name for core version ([7fe8e58](https://github.com/scc-digitalhub/digitalhub-core/commit/7fe8e58c3f25125f84a8bd0fdbca5a7c0466fefd))
* use revision for release version ([3a849c1](https://github.com/scc-digitalhub/digitalhub-core/commit/3a849c1680d3db863e2f6ec2c59402869bf03055))
* validation + search latest api + fixes ([6117fbb](https://github.com/scc-digitalhub/digitalhub-core/commit/6117fbb6d55a0a75a5640d96bc62f02fec6649dd))



# [0.4.0-beta11](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta10...0.4.0-beta11) (2024-05-09)


### Bug Fixes

* add converter to state in entities ([3beff36](https://github.com/scc-digitalhub/digitalhub-core/commit/3beff36aa1041b9e8031a28c7a85b464b617d320))
* fix issues with run fsm and concurrency + various fixes ([34e42f1](https://github.com/scc-digitalhub/digitalhub-core/commit/34e42f1e3c9a6720bc51be3947d622a1af357a5a))



# [0.4.0-beta10](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta9...0.4.0-beta10) (2024-05-09)


### Features

* add registry config shared between k8s and kaniko ([0d9a6ed](https://github.com/scc-digitalhub/digitalhub-core/commit/0d9a6ed75abc6ccf430b93cdf81c9881d637b9a7))



# [0.4.0-beta9](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta8...0.4.0-beta9) (2024-05-07)


### Bug Fixes

* fix labels on k8s objects + remove required imageName from container ([1e5c907](https://github.com/scc-digitalhub/digitalhub-core/commit/1e5c90771c458bffd6547b768d89f38df7fe1563))



# [0.4.0-beta8](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta7...0.4.0-beta8) (2024-05-07)


### Bug Fixes

* store secret for kaniko runs ([5ffba49](https://github.com/scc-digitalhub/digitalhub-core/commit/5ffba496d053fada7aa695c9a0c5378c07ceb376))



# [0.4.0-beta7](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta6...0.4.0-beta7) (2024-05-03)



# [0.4.0-beta3](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.4.0-beta2...0.4.0-beta3) (2024-04-29)


### Features

* secured runnable can transport auth details to frameworks ([d72c2d5](https://github.com/scc-digitalhub/digitalhub-core/commit/d72c2d5be7d956ee3b51a2d920ba7d83cca4f887))



# [0.4.0-beta6](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta5...0.4.0-beta6) (2024-04-30)


### Bug Fixes

* builder tool should skip refs if not set ([e9bd4be](https://github.com/scc-digitalhub/digitalhub-core/commit/e9bd4be69253062611ada9fa7503d392fe977240))
* fix dockerfile building for container runtime ([78ac814](https://github.com/scc-digitalhub/digitalhub-core/commit/78ac814475096a14c59bf953400f7cdc0785d308))


### Features

* update container function image after build via kaniko + clear k8s labels usage ([fedd1b0](https://github.com/scc-digitalhub/digitalhub-core/commit/fedd1b092332155398d4edd05535b4c988ebdb18))



# [0.4.0-beta5](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta4...0.4.0-beta5) (2024-04-30)


### Bug Fixes

* reattach console to upstream ([0ed5389](https://github.com/scc-digitalhub/digitalhub-core/commit/0ed53898c8bffb21a85296b6460752d73a32af24))



# [0.4.0-beta4](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta3...0.4.0-beta4) (2024-04-29)


### Bug Fixes

* update K8sKanikoFramework.java to use image registry ([b0bf5cc](https://github.com/scc-digitalhub/digitalhub-core/commit/b0bf5cc56546e103c701755fe6d663ab04b0cead))



# [0.4.0-beta3](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.4.0-beta2...0.4.0-beta3) (2024-04-29)


### Features

* secured runnable can transport auth details to frameworks ([d72c2d5](https://github.com/scc-digitalhub/digitalhub-core/commit/d72c2d5be7d956ee3b51a2d920ba7d83cca4f887))



# [0.4.0-beta2](https://github.com/scc-digitalhub/digitalhub-core/compare/0.4.0-beta1...v0.4.0-beta2) (2024-04-24)


### Bug Fixes

* fix entity type in workflow indexer ([5b9ee5b](https://github.com/scc-digitalhub/digitalhub-core/commit/5b9ee5b0eeb9b47af02560fc34de6a03ee15e1d4))
* fix oidc config params ([1c08ade](https://github.com/scc-digitalhub/digitalhub-core/commit/1c08ade59dae07141f6c084f45aa171f9540184e))


### Features

* action for building build-tool image ([62b765f](https://github.com/scc-digitalhub/digitalhub-core/commit/62b765f6398f784bde6f4d09c2e98aa8d3ad7772))
* kaniko build infrastructure ([5bd4fed](https://github.com/scc-digitalhub/digitalhub-core/commit/5bd4fed5ebf3765d6117233a4f5d86b11cdbead6))



# [0.4.0-beta1](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.3.0-beta8...0.4.0-beta1) (2024-04-16)


### Bug Fixes

* align kfp kinds and id handling ([241aff4](https://github.com/scc-digitalhub/digitalhub-core/commit/241aff472520d1352e0c545fe936843d91ecd0b5))
* disable request logging filter to avoid breaking request handling ([97c2e28](https://github.com/scc-digitalhub/digitalhub-core/commit/97c2e2833acd59e9f15df2b2028aa79257582eb0))
* fix auditor issue with null authentication on async threads ([77954e9](https://github.com/scc-digitalhub/digitalhub-core/commit/77954e9ab28231c88c2781ea5134f3a2b2f9575b))
* make oauth2 scopes configurable via env ([eec40cd](https://github.com/scc-digitalhub/digitalhub-core/commit/eec40cd60b5109d9db66c0792cc603c8af12c3a8))


### Features

* add cron job execution infrastructure + cleanups ([df983c1](https://github.com/scc-digitalhub/digitalhub-core/commit/df983c135713791b59e56fe83732e788d1f1e6a0))
* add handler to sourceCode in specs ([3b566a2](https://github.com/scc-digitalhub/digitalhub-core/commit/3b566a255b3e53bac130cb648081c9610bf154a5))
* add user filters to search and services ([f3e37f6](https://github.com/scc-digitalhub/digitalhub-core/commit/f3e37f6900696462605f21418971476c1869f246))
* add user to DTOs and audit user in entities + cleanups ([92eaac9](https://github.com/scc-digitalhub/digitalhub-core/commit/92eaac977b38ef8f2ec116d3684b7111093725fd))
* add validation instructions to k8s spec fields ([b1d2ece](https://github.com/scc-digitalhub/digitalhub-core/commit/b1d2ece165512f0b8a9ab3e51146a65e9c25781f))
* core volume as class with enum for volumeTypes ([1cde68f](https://github.com/scc-digitalhub/digitalhub-core/commit/1cde68f156a4d8b9458603d42c3026b203f1b94d))
* enable/disable solr for console via props ([e0a96f3](https://github.com/scc-digitalhub/digitalhub-core/commit/e0a96f363366c65d5fbe760f3cec83999f43a48a))
* indexers and indexable services + update controllers ([a1f5509](https://github.com/scc-digitalhub/digitalhub-core/commit/a1f5509e5075eccb307b84afeac41dec4b3ec355))
* update k8s core resource as (cpu/mem/gpu) ([a1c8623](https://github.com/scc-digitalhub/digitalhub-core/commit/a1c8623dc500e232c8bfa4d82326f73d5f57e45d))
* update project controller to let owners modify/delete owned projects ([1b507e0](https://github.com/scc-digitalhub/digitalhub-core/commit/1b507e0999d1b0be2c0acbe6cbfbf190fd3b0b36))



# [0.3.0-beta8](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.3.0-beta7...v0.3.0-beta8) (2024-04-04)


### Bug Fixes

* fix blob handling in psql ([5dabf59](https://github.com/scc-digitalhub/digitalhub-core/commit/5dabf5920d3528877e8f4fa7676b688ab9d3cc00))



# [0.3.0-beta7](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.3.0-beta6...v0.3.0-beta7) (2024-04-04)


### Bug Fixes

* fix errors in FSM and disable path traversal for more than 1-step state changes ([4a30939](https://github.com/scc-digitalhub/digitalhub-core/commit/4a309393003d714f91909ec3decfdf922cae687e))
* include only non null elements in spec toMap to avoid inserting invalid values in kube + handle find null in store ([b1db674](https://github.com/scc-digitalhub/digitalhub-core/commit/b1db674cb70fca606840bd847fd8b7044dd44774))
* typo in sql ([9748058](https://github.com/scc-digitalhub/digitalhub-core/commit/974805893180c96c98e0b62e470592912f12688e))



# [0.3.0-beta6](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.3.0-beta5...v0.3.0-beta6) (2024-04-02)


### Bug Fixes

* cleanup security config + add auth endpoint for console ([f36d53d](https://github.com/scc-digitalhub/digitalhub-core/commit/f36d53d4215721d81f6c80d26972069683278b46))



# [0.3.0-beta5](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.3.0-beta4...v0.3.0-beta5) (2024-04-02)


### Bug Fixes

* fix issues with jdbc initialization and refactor runnable store ([fb51646](https://github.com/scc-digitalhub/digitalhub-core/commit/fb51646da676f0e4f6025694735c37508d1a16b6))
* fix postgresql schema sql naming ([1adffb0](https://github.com/scc-digitalhub/digitalhub-core/commit/1adffb06cf4e224c4fb6c6c847755aaa339ad1e3))


### Features

* move actuator management endpoint to dedicated port + fix configuration issues ([146c50b](https://github.com/scc-digitalhub/digitalhub-core/commit/146c50b09df785e56274db9a1910915898bf0a8a))
* update specs and define tableSchema ([b0c8bfa](https://github.com/scc-digitalhub/digitalhub-core/commit/b0c8bfad757dd10f63ab648f321b2599e934cda1))



# [0.3.0-beta4](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.3.0-beta3...v0.3.0-beta4) (2024-03-27)



# [0.3.0-beta3](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.0.31...v0.3.0-beta3) (2024-03-26)


### Bug Fixes

* add deleting transaction to all run state ([0b57c91](https://github.com/scc-digitalhub/digitalhub-core/commit/0b57c910a316fe941d51eb1c2a62404c52a383e8))
* align specs and correct implementations ([c9463cc](https://github.com/scc-digitalhub/digitalhub-core/commit/c9463cc2ff430cc4f07f817054e6cfe40c8522c2))
* check for project existence before creating entities ([c1a42dd](https://github.com/scc-digitalhub/digitalhub-core/commit/c1a42dd250d0db8b49310c71d337b4b2dbb524f7))
* check for secret existence before deleting ([39e0964](https://github.com/scc-digitalhub/digitalhub-core/commit/39e0964753f0672bb780748bafbbc9758466104c))
* correct mlrun task job spec ([18d7aa1](https://github.com/scc-digitalhub/digitalhub-core/commit/18d7aa14644bf1e70c86d22ec87144756c2f1782))
* correct wrongly configured pagination handler to start page count from 0 ([ee542d1](https://github.com/scc-digitalhub/digitalhub-core/commit/ee542d18552b2afec39584445ae8328184d05e9c))
* enforce unique secret for name+project ([dd09cbe](https://github.com/scc-digitalhub/digitalhub-core/commit/dd09cbe3041aa76bfe8aaa6f132556dbc078ecb7))
* fix cloud broadcast to rabbit + make async ([c28e46b](https://github.com/scc-digitalhub/digitalhub-core/commit/c28e46b3203ef7297d6868b8f09cc287d05acbc9))
* fix image names in Dockerfile ([9fa9319](https://github.com/scc-digitalhub/digitalhub-core/commit/9fa93192f7227bee78af4ead0b0e50b675fb959a))
* fix mapping for delete all dataItem in context controller ([d9750c4](https://github.com/scc-digitalhub/digitalhub-core/commit/d9750c49178b7c7fe88048aada4a96fcc3641767))
* fix pattern usage in runUtils ([cae0de7](https://github.com/scc-digitalhub/digitalhub-core/commit/cae0de7f521711e9703da198dd69702b5611c3ad))
* fix runmanager wrong states and transactions ([056a7d7](https://github.com/scc-digitalhub/digitalhub-core/commit/056a7d7fbe0da7e254ed7a917253d6536e462b02))
* include frontend in docker builds ([83cd53f](https://github.com/scc-digitalhub/digitalhub-core/commit/83cd53f2fd65f8045bb08eb166f9c32cc401910b))
* include frontend in docker cache ([6bac7e9](https://github.com/scc-digitalhub/digitalhub-core/commit/6bac7e9cc866a6d17f4091a93ff6a3dd5dba2d2c))
* refactor converters implementations and store state enum as string in db ([1b95480](https://github.com/scc-digitalhub/digitalhub-core/commit/1b95480f2f8bd2538dcfaecce6acb23f43bb8c22))
* runnable in events are optional ([5e47826](https://github.com/scc-digitalhub/digitalhub-core/commit/5e478266be28e7b385961f4e9a552c8045ea9d13))
* update base controllers to properly extract search filters + update schema generation ([f519e63](https://github.com/scc-digitalhub/digitalhub-core/commit/f519e634928d43778906208ace80123eb1e0f9c0))
* update dtos and specs ([9381802](https://github.com/scc-digitalhub/digitalhub-core/commit/9381802e170ce9e4bde9cdf5381d049d2b32ce94))
* use localDateTime with iso format for metadata + cleanups ([edcbd44](https://github.com/scc-digitalhub/digitalhub-core/commit/edcbd4438f6cce29928efa8b8eb5330807c2c776))
* use offsetDateTime with iso format for metadata ([bb9495f](https://github.com/scc-digitalhub/digitalhub-core/commit/bb9495fdc59d2b8cf4f8a2ca477bfbac83e01ae4))


### Features

* add LOG_LEVEL env variable to set logging ([5c6c395](https://github.com/scc-digitalhub/digitalhub-core/commit/5c6c3958cdfd5eb969b34a5901ab350d7c953bde))
* align runtimes to run manager + fix and align k8s frameworks + add run actions RPC to context controller ([5a47880](https://github.com/scc-digitalhub/digitalhub-core/commit/5a478809f1f78b029fe72536560e52e0827de1e8))
* async delete listener for runs ([afcae53](https://github.com/scc-digitalhub/digitalhub-core/commit/afcae530719967afcd053d6b1779a892926b60e7))
* delete runs via manager and properly handle cleanups in runtimes ([3d75f4b](https://github.com/scc-digitalhub/digitalhub-core/commit/3d75f4b22a15bd7d3c033f01130ec71820a96590))
* handle common exceptions in controllerAdvice ([e904621](https://github.com/scc-digitalhub/digitalhub-core/commit/e9046211685c9fa01dcb403e41771a7a2f86d65b))
* integrate user console + fix request handling for application ([1ccbe0f](https://github.com/scc-digitalhub/digitalhub-core/commit/1ccbe0f82bbd22ec94486c18dafee9518d8112fb))
* KFP remote implementation ([#71](https://github.com/scc-digitalhub/digitalhub-core/issues/71)) ([e6eb6a2](https://github.com/scc-digitalhub/digitalhub-core/commit/e6eb6a20832c2611916f90d5cf8896c8a78dad1d))
* sourceCode in spec supports a list of languages ([8c1db42](https://github.com/scc-digitalhub/digitalhub-core/commit/8c1db42ac9841bb34bdb37d1196c92777877d206))
* update specs (BREAKING) ([0429407](https://github.com/scc-digitalhub/digitalhub-core/commit/04294076d037755cd3dbb932fde651e31c357022))



## [0.0.31](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.0.30...v0.0.31) (2024-03-08)


### Bug Fixes

* update security config to enable ROLE_ADMIN access from oidc ([5269937](https://github.com/scc-digitalhub/digitalhub-core/commit/526993711367f714a4eca2bb0ac9919857793fc4))


### Features

* add store key to all dtos ([1bd0b38](https://github.com/scc-digitalhub/digitalhub-core/commit/1bd0b381cf6715faef6c8a267ab063f12d3e0021))



## [0.0.30](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.29...v0.0.30) (2024-03-04)


### Bug Fixes

* add pathvariable to schemaController.get ([ea18b12](https://github.com/scc-digitalhub/digitalhub-core/commit/ea18b12f374a8cd06231428ae52f056d6059d832))
* align run usage with models + handle store exceptions ([d6a36d4](https://github.com/scc-digitalhub/digitalhub-core/commit/d6a36d4464bcc093619891a76d9e5ee212a7897a))
* update run.sh script ([9bf299e](https://github.com/scc-digitalhub/digitalhub-core/commit/9bf299e0fd409037423e1f9083ea96e59a0257d0))


### Features

* banner + correct version number in app ([3b335d8](https://github.com/scc-digitalhub/digitalhub-core/commit/3b335d8542ec6cf0b45dcc35a49f597b685ac5c5))
* cronJob k8s support + cleanups ([caa4278](https://github.com/scc-digitalhub/digitalhub-core/commit/caa4278e2978b7aca5010d05a81bf5f973ef1987))



## [0.0.29](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.28...0.0.29) (2024-02-21)


### Bug Fixes

* add converter for entityName enum with value, supporting lowercase+ pluralization ([e867eb2](https://github.com/scc-digitalhub/digitalhub-core/commit/e867eb216c1ef180b4d17eb30c958961e0776d40))
* cache dockerfile ([8c32692](https://github.com/scc-digitalhub/digitalhub-core/commit/8c326924611b10f822af7ccc4649b2100418ebca))
* use conditional kube annotation for k8s stores ([a571ebb](https://github.com/scc-digitalhub/digitalhub-core/commit/a571ebb9bd603f6e2167c447e722b2f3ba91f120))


### Features

* auth annotations with roles on controllers ([2de6ca2](https://github.com/scc-digitalhub/digitalhub-core/commit/2de6ca2d6452f1cae77521969c9efed9aa48a84f))



## [0.0.28](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.27...0.0.28) (2024-02-21)



## [0.0.27](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.26...0.0.27) (2024-02-20)


### Bug Fixes

* add constructor to dataitem base spec ([57d578d](https://github.com/scc-digitalhub/digitalhub-core/commit/57d578d3e015bfe4855ab1edd1d9e322deefbc78))
* change maven image tag ([a70a19c](https://github.com/scc-digitalhub/digitalhub-core/commit/a70a19cb750f68da18a889afd7e922a6fa66b3d8))
* correct default db location ([dcdb033](https://github.com/scc-digitalhub/digitalhub-core/commit/dcdb0331d200b0b84c528c2fb3c02e68f9591c2a))


### Features

* add runtime to specs + add runtime filtering to schemas via registry ([a78173b](https://github.com/scc-digitalhub/digitalhub-core/commit/a78173bdfa50428e401ed49a364099ee31abe1db))
* add schema handling with registry + controllers + utils ([c742079](https://github.com/scc-digitalhub/digitalhub-core/commit/c742079b463c004d487f887ecf4482a6ad098cd2))
* allow runs to override k8s specs defined in tasks ([a7177de](https://github.com/scc-digitalhub/digitalhub-core/commit/a7177deb54328f8ed026ad3134f14550e93f707b))
* auto build images on tags ([153f951](https://github.com/scc-digitalhub/digitalhub-core/commit/153f951166669194258ea97f4d210ff1540df3bb))



## [0.0.26](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.25...0.0.26) (2024-02-12)



## [0.0.25](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.24...0.0.25) (2024-02-06)



## [0.0.24](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.23...0.0.24) (2024-02-06)



## [0.0.23](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.22...0.0.23) (2024-02-05)



## [0.0.22](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.21...0.0.22) (2024-01-25)



## [0.0.21](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.20...0.0.21) (2024-01-25)



## [0.0.20](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.19...0.0.20) (2024-01-25)



## [0.0.19](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.18...0.0.19) (2024-01-25)



## [0.0.18](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.17...0.0.18) (2024-01-24)



## [0.0.17](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.16...0.0.17) (2024-01-22)



## [0.0.16](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.15...0.0.16) (2024-01-22)



## [0.0.15](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.14...0.0.15) (2024-01-22)



## [0.0.14](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.13...0.0.14) (2024-01-22)



## [0.0.13](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.12...0.0.13) (2024-01-19)



## [0.0.12](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.11...0.0.12) (2024-01-18)



## [0.0.11](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.10...0.0.11) (2023-12-13)



## [0.0.10](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.9...0.0.10) (2023-12-12)



## [0.0.9](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.8...0.0.9) (2023-12-12)



## [0.0.8](https://github.com/scc-digitalhub/digitalhub-core/compare/0.0.6...0.0.8) (2023-12-11)



## [0.0.4](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.0.3-snapshot...0.0.4) (2023-11-14)



## [0.0.3-snapshot](https://github.com/scc-digitalhub/digitalhub-core/compare/v0.0.2-snapshot...v0.0.3-snapshot) (2023-09-19)



## 0.0.2-snapshot (2023-07-11)



