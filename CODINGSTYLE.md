Coding Style
-------------

I (Ji) made this to prevent confusion between our code.

ALL STRINGs and string arrays or whatever MUST in xml files in values folder ex) strings.xml 
this makes our life easier! later on we can display every string in user picked language
don't forget that this is applied for object properties ex) Button.text, TextView.text


### Name Convention 
* (lower) camel case for functions / variables
  - buttonNameLikeThis, textviewLikeThis
* (upper) camel case for class names
  - ClassNameLikeThis
* long and useful name is good
  - ex) stringEventName is better than name

### Events
  * All events should be on separated fuction

Coding Examples
--------------

### Java (Android)

#### Acceptable

```java
public void changeText(View v) {
       textviewEventMessage.setText("you just clicked.");
}
```

#### Bad example

```java
btnPlus.setOnClickListener(new View.OnClickListener() {
                               public void onClick(View v) {
                                    TextView score = (TextView) findViewById(R.id.tvScore);
                                    int newScore = (Integer.parseInt(score.getText().toString())+1);
                                    score.setText( Integer.toString(newScore) );
                                }
} 
```

### XML

* Rather than directly accessing XML, try to use the property and graphical feature provided by Android SDK

```xml
<Button
  android:id="@+id/buttonChangeText"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:text="@string/changeText" 
  android:onClick="changeText"/>
```


