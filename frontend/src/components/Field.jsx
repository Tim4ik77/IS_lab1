import { useId } from 'react';
import { labels, refLabel, titles } from '../schema';

export default function Field({ field, value, onChange, references, error }) {
  const id = `${useId()}-${field.key}`;
  return (
    <label className="field" htmlFor={id}>
      <span>
        {field.label}
        {field.required && !field.allowEmpty ? <b> *</b> : <small> · необязательно</small>}
      </span>
      {field.kind === 'select' || field.kind === 'ref' ? (
        <select
          id={id}
          value={value}
          required={field.required}
          onChange={(e) => onChange(e.target.value)}
          aria-invalid={!!error}
        >
          <option value="">{field.required ? 'Выберите…' : 'Не указано'}</option>
          {field.kind === 'select'
            ? field.values.map((v) => (
                <option key={v} value={v}>
                  {labels[v] || v}
                </option>
              ))
            : (references[field.source] || []).map((item) => (
                <option key={item.id} value={item.id}>
                  {refLabel(field.source, item)}
                </option>
              ))}
        </select>
      ) : (
        <input
          id={id}
          type={field.kind}
          value={value}
          required={field.required && !field.allowEmpty}
          min={field.min}
          max={field.max}
          step={field.step || (field.kind === 'number' ? 1 : undefined)}
          onChange={(e) => onChange(e.target.value)}
          aria-invalid={!!error}
        />
      )}
      {field.kind === 'ref' && !references[field.source]?.length && (
        <small>
          Сначала добавьте объект в разделе «Связанные объекты» → {titles[field.source]}.
        </small>
      )}
      {error && <small className="field-error">{error}</small>}
    </label>
  );
}
