# 此项目仿照[Glide](https://github.com/bumptech/glide)编写，目的是方便在Harmony上，可以轻松的加载及绘制图片。

关于LICENSE我不太懂，如果有问题，请及时联系我，我会及时修改。

### ToyCar

起名叫ToyCar

一.是因为相比Glide，ToyCar各种实现并不成熟，还有很多需要改进的地方，更像是一个玩具。

二.是因为我家的小朋友很喜欢玩具车，我希望这个库也能受到大家的喜欢。

### Setup
Download
```groovy
implementation 'com.github.boxuanjia:toycar:0.0.1'
```

Initialize
```java
ToyCar.initialize(this);
```
And use
```java
ToyCar.load("https://developer.harmonyos.com/resource/image/DevEco-Studio/DevEco-Studio-0.png").into(image);
```

### License
<pre>
Copyright 2020 boxuanjia

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
