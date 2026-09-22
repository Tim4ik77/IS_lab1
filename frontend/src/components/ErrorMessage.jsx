import { schemas } from '../schema';

const fieldLabels = {};
for (const fields of Object.values(schemas)) {
  for (const field of fields) {
    fieldLabels[field.key] ??= field.label;
  }
}

export default function ErrorMessage({ error }) {
  if (!error) return null;
  return (
    <div className="error" role="alert">
      {error.message || String(error)}
      {error.fields && Object.keys(error.fields).length > 0 && (
        <ul>
          {Object.entries(error.fields).map(([key, value]) => (
            <li key={key}>
              {fieldLabels[key] || key}: {value}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
