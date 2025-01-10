export interface NormalizedEntity<T> {
  byId: { [key: number]: T };
  allIds: number[];
}

export const normalizeEntities = <T extends { id: number }>(
  entities: T[]
): NormalizedEntity<T> => {
  return entities.reduce(
    (acc, entity) => {
      acc.byId[entity.id] = entity;
      if (!acc.allIds.includes(entity.id)) {
        acc.allIds.push(entity.id);
      }
      return acc;
    },
    { byId: {}, allIds: [] } as NormalizedEntity<T>
  );
};

export const denormalizeEntities = <T>(
  normalizedEntity: NormalizedEntity<T>
): T[] => {
  return normalizedEntity.allIds.map((id) => normalizedEntity.byId[id]);
};

export const addEntity = <T extends { id: number }>(
  state: NormalizedEntity<T>,
  entity: T
): NormalizedEntity<T> => {
  if (state.byId[entity.id]) {
    return {
      ...state,
      byId: {
        ...state.byId,
        [entity.id]: entity,
      },
    };
  }

  return {
    byId: {
      ...state.byId,
      [entity.id]: entity,
    },
    allIds: [...state.allIds, entity.id],
  };
};

export const updateEntity = <T extends { id: number }>(
  state: NormalizedEntity<T>,
  entity: T
): NormalizedEntity<T> => {
  if (!state.byId[entity.id]) {
    return state;
  }

  return {
    ...state,
    byId: {
      ...state.byId,
      [entity.id]: entity,
    },
  };
};

export const removeEntity = <T extends { id: number }>(
  state: NormalizedEntity<T>,
  entityId: number
): NormalizedEntity<T> => {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { [entityId]: _, ...byId } = state.byId;
  return {
    byId,
    allIds: state.allIds.filter((id) => id !== entityId),
  };
};

export const addEntities = <T extends { id: number }>(
  state: NormalizedEntity<T>,
  entities: T[]
): NormalizedEntity<T> => {
  return entities.reduce((acc, entity) => addEntity(acc, entity), state);
};

// 创建选择器
export const createEntitySelectors = <T extends { id: number }, S>(
  selectState: (state: S) => NormalizedEntity<T>
) => {
  const selectIds = (state: S) => selectState(state).allIds;
  const selectById = (state: S, id: number) => selectState(state).byId[id];
  const selectAll = (state: S) => denormalizeEntities(selectState(state));
  const selectTotal = (state: S) => selectState(state).allIds.length;

  return {
    selectIds,
    selectById,
    selectAll,
    selectTotal,
  };
}; 