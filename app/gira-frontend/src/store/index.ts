import { configureStore, Reducer } from '@reduxjs/toolkit';
import { persistStore, persistReducer, PersistConfig } from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import authReducer from './slices/authSlice';
import boardReducer from './slices/boardSlice';
import sprintReducer from './slices/sprintSlice';
import userReducer from './slices/userSlice';
import { RootState } from './types';

const authPersistConfig: PersistConfig<RootState['auth']> = {
  key: 'auth',
  storage,
  whitelist: ['token', 'user'],
};

const store = configureStore({
  reducer: {
    auth: persistReducer(authPersistConfig, authReducer) as Reducer<RootState['auth']>,
    board: boardReducer,
    sprint: sprintReducer,
    users: userReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST'],
      },
    }),
});

export type AppDispatch = typeof store.dispatch;

export { store, type RootState };
export const persistor = persistStore(store); 