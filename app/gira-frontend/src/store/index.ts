import { configureStore } from '@reduxjs/toolkit';
import { persistStore, persistReducer, PersistConfig } from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import authReducer, { AuthState } from './slices/authSlice';
import boardReducer from './slices/boardSlice';
import sprintReducer from './slices/sprintSlice';
import userReducer from './slices/userSlice';
import { RootState } from './types';

const authPersistConfig: PersistConfig<AuthState> = {
  key: 'auth',
  storage,
  whitelist: ['token', 'user'],
};

const persistedAuthReducer = persistReducer(authPersistConfig, authReducer);

const store = configureStore({
  reducer: {
    auth: persistedAuthReducer,
    board: boardReducer,
    sprint: sprintReducer,
    users: userReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [
          'persist/PERSIST',
          'persist/REHYDRATE',
          'persist/PURGE'
        ],
      },
    }),
});

export type AppDispatch = typeof store.dispatch;

export { store, type RootState };
export const persistor = persistStore(store); 