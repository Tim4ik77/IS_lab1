import { useState, useRef } from 'react';
import { api } from '../api';
import useRefresh from '../hooks/useRefresh';
import ErrorMessage from '../components/ErrorMessage';
import Field from '../components/Field';
import OperationResult from '../components/OperationResult';
import { operationFields } from '../schema';

export default function OperationsPage({ references, onChanged, onEdit, onDelete, revision }) {
  const operations = [
    ['count', 'Количество по площадке', 'Посчитать билеты с ID площадки больше выбранного.'],
    [
      'prefix',
      'Поиск по началу имени',
      'Найти билеты, название которых начинается с указанного текста.',
    ],
    ['less', 'Билеты по площадке', 'Найти билеты с ID площадки меньше выбранного.'],
    ['sell', 'Продать билет', 'Назначить человека и установить указанную цену продажи.'],
    [
      'clone',
      'Копия со скидкой',
      'Создать новый билет, указать скидку и увеличить исходную цену на тот же процент.',
    ],
  ];
  const [selected, setSelected] = useState('count');
  const [values, setValues] = useState({
    venueId: '',
    prefix: '',
    ticketId: '',
    personId: '',
    amount: '',
    discount: '',
  });
  const [result, setResult] = useState(null);
  const [query, setQuery] = useState(null);
  const [error, setError] = useState(null);
  const [busy, setBusy] = useState(false);
  const generation = useRef(0);
  const fields = operationFields[selected];

  useRefresh(
    async (active) => {
      if (!query) return;
      const current = generation.current;
      try {
        const response = await api(query);
        if (active() && current === generation.current) {
          setResult(response);
          setError(null);
        }
      } catch (e) {
        if (active() && current === generation.current) {
          setError(e);
          setResult(null);
        }
      }
    },
    [query, revision],
  );
  async function run(event) {
    event.preventDefault();
    setBusy(true);
    setError(null);
    setQuery(null);
    setResult(null);
    const current = ++generation.current;
    try {
      let path;
      let response;
      if (selected === 'count') path = `/operations/count-venue-greater?venueId=${values.venueId}`;
      if (selected === 'less') path = `/operations/venue-less?venueId=${values.venueId}`;
      if (selected === 'prefix')
        path = `/operations/name-prefix?prefix=${encodeURIComponent(values.prefix)}`;
      if (path) {
        response = await api(path);
      } else {
        response = await api(`/operations/${selected}`, {
          method: 'POST',
          body:
            selected === 'sell'
              ? { ticketId: values.ticketId, personId: values.personId, amount: values.amount }
              : { ticketId: values.ticketId, discount: values.discount },
        });
        path = `/tickets/${response.id}`;
        onChanged();
      }
      if (current === generation.current) {
        setResult(response);
        setQuery(path);
      }
    } catch (e) {
      if (current === generation.current) setError(e);
    } finally {
      setBusy(false);
    }
  }
  const current = operations.find((o) => o[0] === selected);
  return (
    <>
      <div className="toolbar">
        <div>
          <h1>Специальные операции</h1>
        </div>
      </div>
      <form className="operation-form" onSubmit={run}>
        <label>
          Операция
          <select
            value={selected}
            disabled={busy}
            onChange={(event) => {
              generation.current++;
              setSelected(event.target.value);
              setResult(null);
              setQuery(null);
              setError(null);
            }}
          >
            {operations.map(([key, title]) => (
              <option key={key} value={key}>
                {title}
              </option>
            ))}
          </select>
        </label>
        <p>{current[2]}</p>
        <ErrorMessage error={error} />
        <div className="form-grid">
          {fields.map((field) => (
            <Field
              key={field.key}
              field={field}
              value={values[field.key]}
              onChange={(value) => setValues((previous) => ({ ...previous, [field.key]: value }))}
              references={references}
              error={error?.fields?.[field.key]}
            />
          ))}
        </div>
        {selected === 'clone' && (
          <p>Новая цена = исходная цена × (100 + скидка) / 100, с округлением до целого.</p>
        )}
        <button disabled={busy}>{busy ? 'Выполнение…' : 'Выполнить'}</button>
      </form>
      <OperationResult
        key={query || selected}
        result={result}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    </>
  );
}
