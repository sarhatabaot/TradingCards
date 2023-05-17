# ☕ Java API

We use jitpack for our repository.

{% tabs %}
{% tab title="Maven" %}
{% code title="pom.xml - repository" %}
```xml
<repository>
	<id>jitpack.io</id>
	<url>https://jitpack.io</url>
</repository>
```
{% endcode %}

{% code title="pom.xml - artifact" %}
```xml
<dependency>
	<groupId>com.github.sarhatabaot.TradingCards</groupId>
	<artifactId>tradingcards-api</artifactId>
	<version>5.7.2</version>
</dependency>
```
{% endcode %}
{% endtab %}

{% tab title="Gradle (Groovy)" %}
<pre class="language-groovy" data-title="build.gradle - repository"><code class="lang-groovy">repositories {
<strong>    maven { url 'https://jitpack.io' }
</strong>}
</code></pre>

{% code title="build.gradle - dependency" %}
```groovy
dependencies {
    implementation 'com.github.sarhatabaot.TradingCards:tradingcards-api:5.7.2'
}
```
{% endcode %}
{% endtab %}

{% tab title="Gradle (Kotlin)" %}
{% code title="build.gradle.kts - repository" %}
```kts
repositories {
    maven("https://jitpack.io")
}
```
{% endcode %}

{% code title="build.gradle.kts - dependency" %}
```kts
dependencies {
    compileOnly ("com.github.sarhatabaot.TradingCards:tradingcards-api:5.7.2")
}
```
{% endcode %}
{% endtab %}
{% endtabs %}
