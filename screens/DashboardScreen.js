import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  FlatList,
  Linking,
  Alert,
  Image,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {NativeModules} from 'react-native';

const {WidgetModule} = NativeModules;

export default function DashboardScreen({navigation}) {
  const icons = {
    youtube: require('../assets/icons/youtube.png'),
    github: require('../assets/icons/github.png'),
    linkedin: require('../assets/icons/linkedin.png'),
    leetcode: require('../assets/icons/leetcode.png'),
    instagram: require('../assets/icons/instagram.png'),
    default: require('../assets/icons/link.png'),
  };

  const [links, setLinks] = useState([]);

  // Color selection logic
  const getCardColor = url => {
    if (!url) return '#1A1A1A'; 
    const lowerUrl = url.toLowerCase();

    if (lowerUrl.includes('youtube') || lowerUrl.includes('youtu.be')) {
      return '#de0000'; // Red
    }
    if (lowerUrl.includes('github')) {
      return '#000000'; // Black
    }
    if (lowerUrl.includes('linkedin')) {
      return '#006de1'; // Blue
    }
    if (lowerUrl.includes('leetcode')) {
      return '#ec9c08'; // Orange
    }
    if (lowerUrl.includes('instagram')) {
      return '#df088d'; // Magenta
    }

    return '#1A1A1A'; // Generic links background
  };

  // Precise string checking condition matchers
  const getIcon = url => {
    if (!url) return icons.default;
    const lowerUrl = url.toLowerCase();

    if (lowerUrl.includes('youtube') || lowerUrl.includes('youtu.be')) {
      return icons.youtube;
    }
    if (lowerUrl.includes('github')) {
      return icons.github;
    }
    if (lowerUrl.includes('linkedin')) {
      return icons.linkedin;
    }
    if (lowerUrl.includes('leetcode')) {
      return icons.leetcode;
    }
    if (lowerUrl.includes('instagram')) {
      return icons.instagram;
    }

    return icons.default;
  };

  const saveLinks = async updatedLinks => {
    try {
      const jsonData = JSON.stringify(updatedLinks);
      await AsyncStorage.setItem('links', jsonData);

      if (WidgetModule?.saveLinks) {
        WidgetModule.saveLinks(jsonData);
      }
    } catch (error) {
      console.log(error);
    }
  };

  const loadLinks = async () => {
    try {
      const storedLinks = await AsyncStorage.getItem('links');
      if (storedLinks) {
        setLinks(JSON.parse(storedLinks));
      }
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    loadLinks();
  }, []);

  const showOptions = item => {
    Alert.alert(
      item.title,
      'Choose an action',
      [
        {
          text: 'Edit',
          onPress: () =>
            navigation.navigate('AddLink', {
              editLink: item,
              onSave: updatedLink => {
                const updatedLinks = links.map(link =>
                  link.id === updatedLink.id ? updatedLink : link,
                );
                setLinks(updatedLinks);
                saveLinks(updatedLinks);
              },
            }),
        },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: async () => {
            const updatedLinks = links.filter(x => x.id !== item.id);
            setLinks(updatedLinks);
            await saveLinks(updatedLinks);
          },
        },
        {
          text: 'Cancel',
          style: 'cancel',
        },
      ],
    );
  };

  const openLink = async url => {
    try {
      if (url) {
        await Linking.openURL(url);
      }
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <View style={styles.container}>
      {/* REBRANDED: Replaced Study Launcher with Zurl */}
      <Text style={styles.title}>Zurl</Text>

      <TouchableOpacity
        style={styles.addButton}
        onPress={() =>
          navigation.navigate('AddLink', {
            onSave: newLink => {
              const updatedLinks = [...links, newLink];
              setLinks(updatedLinks);
              saveLinks(updatedLinks);
            },
          })
        }>
        <Text style={styles.buttonText}>+ Add Link</Text>
      </TouchableOpacity>

      <FlatList
        data={links}
        keyExtractor={item => item.id}
        renderItem={({item}) => (
          <TouchableOpacity
            style={[
              styles.card,
              {
                backgroundColor: getCardColor(item.url),
              },
            ]}
            onPress={() => openLink(item.url)}
            onLongPress={() => showOptions(item)}>
            <View style={styles.titleRow}>
              <Image
                source={getIcon(item.url)}
                style={styles.icon}
                resizeMode="contain"
              />
              <Text style={styles.cardTitle}>{item.title}</Text>
            </View>
            {/* CLEANED: Removed the subtitle domain text from here completely */}
          </TouchableOpacity>
        )}
        ListEmptyComponent={
          <Text style={styles.emptyText}>No links added yet</Text>
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#121212',
    padding: 20,
  },
  title: {
    color: '#fff',
    fontSize: 32,
    fontWeight: 'bold',
    marginBottom: 20,
    letterSpacing: 0.5, // Sleek brand tracking look
  },
  addButton: {
    backgroundColor: '#4F46E5',
    padding: 15,
    borderRadius: 14,
    marginBottom: 20,
  },
  buttonText: {
    color: '#fff',
    textAlign: 'center',
    fontSize: 18,
    fontWeight: 'bold',
  },
  card: {
    paddingVertical: 20, // Slightly improved padding balance since subtitle is gone
    paddingHorizontal: 18,
    borderRadius: 16,
    marginBottom: 14,
    elevation: 3,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
  },
  titleRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  icon: {
    width: 28,
    height: 28,
    marginRight: 10,
  },
  cardTitle: {
    color: '#ffffff',
    fontSize: 20,
    fontWeight: 'bold',
    flex: 1,
  },
  emptyText: {
    color: '#888',
    textAlign: 'center',
    marginTop: 30,
    fontSize: 16,
  },
});