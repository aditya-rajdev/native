```import React, {useState} from 'react';
import {
View,
Text,
TextInput,
TouchableOpacity,
StyleSheet,
} from 'react-native';

export default function EditLinkScreen({
route,
navigation,
}) {
const {link} = route.params;

const [title, setTitle] = useState(link.title);
const [url, setUrl] = useState(link.url);

const handleUpdate = () => {
navigation.navigate('Dashboard', {
updatedLink: {
...link,
title,
url,
},
});
};

return ( <View style={styles.container}> <Text style={styles.heading}>Edit Link</Text>


  <TextInput
    style={styles.input}
    value={title}
    onChangeText={setTitle}
  />

  <TextInput
    style={styles.input}
    value={url}
    onChangeText={setUrl}
  />

  <TouchableOpacity
    style={styles.button}
    onPress={handleUpdate}>
    <Text style={styles.buttonText}>
      Update Link
    </Text>
  </TouchableOpacity>
</View>


);
}

const styles = StyleSheet.create({
container: {
flex: 1,
padding: 20,
backgroundColor: '#121212',
},
heading: {
color: '#fff',
fontSize: 24,
fontWeight: 'bold',
marginBottom: 20,
},
input: {
backgroundColor: '#1E1E1E',
color: '#fff',
height: 50,
borderRadius: 10,
paddingHorizontal: 15,
marginBottom: 15,
},
button: {
backgroundColor: '#4F46E5',
height: 50,
borderRadius: 10,
justifyContent: 'center',
alignItems: 'center',
},
buttonText: {
color: '#fff',
fontWeight: 'bold',
},
});
```