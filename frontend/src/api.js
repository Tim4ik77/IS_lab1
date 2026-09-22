export async function api(path, options = {}) {
  let response;
  try {
    response = await fetch(`/api${path}`, {
      ...options,
      headers: { 'Content-Type': 'application/json', ...options.headers },
      body: options.body === undefined ? undefined : JSON.stringify(options.body),
    });
  } catch {
    throw new Error('Не удалось подключиться к серверу. Проверьте, что он запущен.');
  }
  if (response.status === 204) return null;
  const data = await response.json().catch(() => null);
  if (!response.ok) {
    const error = new Error(data?.message || `Ошибка сервера (${response.status})`);
    error.fields = data?.fields || {};
    throw error;
  }
  return data;
}

export const referenceKinds = ['coordinates', 'locations', 'persons', 'events', 'venues'];
export async function getReferences() {
  const results = await Promise.all(referenceKinds.map((kind) => api(`/references/${kind}`)));
  return Object.fromEntries(referenceKinds.map((kind, index) => [kind, results[index]]));
}
