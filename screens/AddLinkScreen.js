
import React, {useState} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
} from 'react-native';

const AddLinkScreen = ({navigation, route}) => {
  const editingLink = route?.params?.editLink;

  const [title, setTitle] = useState(
    editingLink?.title || '',
  );

  const [url, setUrl] = useState(
    editingLink?.url || '',
  );

  const handleSave = () => {
    if (!title.trim() || !url.trim()) {
      Alert.alert(
        'Error',
        'Please fill all fields',
      );
      return;
    }

    const linkData = {
      id: editingLink
        ? editingLink.id
        : Date.now().toString(),
      title: title.trim(),
      url: url.trim(),
    };

    route?.params?.onSave?.(linkData);

    navigation.goBack();
  };

  return (
    <View style={styles.container}>
      <Text style={styles.heading}>
        {editingLink
          ? 'Edit Link'
          : 'Add New Link'}
      </Text>

      <TextInput
        placeholder="Enter Title"
        placeholderTextColor="#888"
        style={styles.input}
        value={title}
        onChangeText={setTitle}
      />

      <TextInput
        placeholder="https://example.com"
        placeholderTextColor="#888"
        style={styles.input}
        value={url}
        onChangeText={setUrl}
        autoCapitalize="none"
        autoCorrect={false}
      />

      <TouchableOpacity
        style={styles.button}
        onPress={handleSave}>
        <Text style={styles.buttonText}>
          {editingLink
            ? 'Update Link'
            : 'Save Link'}
        </Text>
      </TouchableOpacity>
    </View>
  );
};

export default AddLinkScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#121212',
    padding: 20,
  },

  heading: {
    color: '#fff',
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 25,
  },

  input: {
    backgroundColor: '#1E1E1E',
    color: '#fff',
    borderRadius: 12,
    paddingHorizontal: 15,
    height: 55,
    marginBottom: 15,
    fontSize: 16,
  },

  button: {
    backgroundColor: '#4F46E5',
    height: 55,
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 5,
  },

  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
