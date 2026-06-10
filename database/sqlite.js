import SQLite from 'react-native-sqlite-storage';

const db = SQLite.openDatabase(
{
name: 'studylauncher.db',
location: 'default',
},
() => {
console.log('Database Opened');
},
error => {
console.log(error);
},
);

export default null;
