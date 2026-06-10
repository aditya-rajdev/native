
import React from 'react';
import {createNativeStackNavigator} from '@react-navigation/native-stack';

import DashboardScreen from '../screens/DashboardScreen';
import AddLinkScreen from '../screens/AddLinkScreen';

const Stack = createNativeStackNavigator();

export default function AppNavigator() {
  return (
    <Stack.Navigator
      screenOptions={{
        headerStyle: {
          backgroundColor: '#121212',
        },
        headerTintColor: '#fff',
      }}>
      
      <Stack.Screen
        name="Dashboard"
        component={DashboardScreen}
        options={{
          title: 'Zurl',
        }}
      />

      <Stack.Screen
        name="AddLink"
        component={AddLinkScreen}
        options={{
          title: 'Add Link',
        }}
      />

    </Stack.Navigator>
  );
}