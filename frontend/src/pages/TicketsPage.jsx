import { useState } from 'react';
import { api } from '../api';
import { labels } from '../schema';
import useRefresh from '../hooks/useRefresh';
import ErrorMessage from '../components/ErrorMessage';
import TicketTable from '../components/TicketTable';

export default function TicketsPage({ revision, onCreate, onEdit, onDelete }) {
  const [data, setData] = useState({ items: [], total: 0 });
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sort, setSort] = useState('id');
  const [direction, setDirection] = useState('asc');
  const [column, setColumn] = useState('name');
  const [value, setValue] = useState('');
  const [filters, setFilters] = useState({});
  const [id, setId] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const options = {
    id: 'ID',
    name: 'Название билета',
    eventName: 'Название события',
    venueName: 'Название площадки',
    locationName: 'Название локации',
    type: 'Тип билета',
  };
  useRefresh(
    async (active) => {
      try {
        const response = await api(
          `/tickets?${new URLSearchParams({ page, size, sort, direction, ...filters })}`,
        );
        if (!active()) return;
        if (page > 0 && page * size >= Number(response.total))
          setPage(Math.max(0, Math.ceil(Number(response.total) / size) - 1));
        setData(response);
        setError(null);
      } catch (e) {
        if (active()) setError(e);
      } finally {
        if (active()) setLoading(false);
      }
    },
    [page, size, sort, direction, JSON.stringify(filters), revision],
  );
  return (
    <>
      <div className="toolbar">
        <div>
          <h1>
            Билеты <span>{data.total}</span>
          </h1>
        </div>
        <button onClick={onCreate}>Создать билет</button>
      </div>
      <div>
        <form
          className="toolbar"
          onSubmit={(e) => {
            e.preventDefault();
            setFilters((f) => ({ ...f, [column]: value }));
            setPage(0);
          }}
        >
          <label>
            Поле
            <select
              value={column}
              onChange={(e) => {
                setColumn(e.target.value);
                setValue('');
              }}
            >
              {Object.entries(options)
                .filter(([k]) => k !== 'id')
                .map(([k, v]) => (
                  <option key={k} value={k}>
                    {v}
                  </option>
                ))}
            </select>
          </label>
          <label>
            Точное совпадение
            {column === 'type' ? (
              <select value={value} onChange={(e) => setValue(e.target.value)} required>
                <option value="">Выберите тип</option>
                {['VIP', 'USUAL', 'CHEAP'].map((v) => (
                  <option value={v} key={v}>
                    {labels[v]}
                  </option>
                ))}
              </select>
            ) : (
              <input
                placeholder="Введите полное значение"
                value={value}
                onChange={(e) => setValue(e.target.value)}
              />
            )}
          </label>
          <button>Применить</button>
          <label>
            Сортировка
            <select
              value={sort}
              onChange={(e) => {
                setSort(e.target.value);
                setPage(0);
              }}
            >
              {Object.entries(options).map(([k, v]) => (
                <option key={k} value={k}>
                  {v}
                </option>
              ))}
            </select>
          </label>
          <button
            type="button"
            onClick={() => {
              setDirection((d) => (d === 'asc' ? 'desc' : 'asc'));
              setPage(0);
            }}
            aria-label="Изменить направление сортировки"
          >
            {direction === 'asc' ? '↑ По возрастанию' : '↓ По убыванию'}
          </button>
        </form>
        {Object.keys(filters).length > 0 && (
          <div className="toolbar">
            {Object.entries(filters).map(([k, v]) => (
              <button
                key={k}
                onClick={() => {
                  setFilters((f) => {
                    const next = { ...f };
                    delete next[k];
                    return next;
                  });
                  setPage(0);
                }}
              >
                {options[k]}: {v || '(пусто)'} ×
              </button>
            ))}
            <button
              onClick={() => {
                setFilters({});
                setPage(0);
              }}
            >
              Сбросить всё
            </button>
          </div>
        )}
        <ErrorMessage error={error} />
        {loading ? (
          <div>Загружаем билеты…</div>
        ) : (
          <TicketTable items={data.items} onEdit={onEdit} onDelete={onDelete} />
        )}
        <div className="pagination">
          <span>
            {Number(data.total)
              ? `${page * size + 1}–${Math.min((page + 1) * size, Number(data.total))} из ${data.total}`
              : '0 билетов'}
          </span>
          <div>
            <label>
              На странице{' '}
              <select
                value={size}
                onChange={(e) => {
                  setSize(Number(e.target.value));
                  setPage(0);
                }}
              >
                {[10, 20, 50].map((s) => (
                  <option key={s}>{s}</option>
                ))}
              </select>
            </label>
            <button disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
              ←
            </button>
            <span>
              {page + 1} / {Math.max(1, Math.ceil(Number(data.total) / size))}
            </span>
            <button
              disabled={(page + 1) * size >= Number(data.total)}
              onClick={() => setPage((p) => p + 1)}
            >
              →
            </button>
          </div>
        </div>
      </div>
      <form
        className="toolbar"
        onSubmit={(e) => {
          e.preventDefault();
          location.hash = `/tickets/${id}`;
        }}
      >
        <label htmlFor="lookup-id">Открыть билет по ID</label>
        <input
          id="lookup-id"
          type="number"
          min="1"
          step="1"
          required
          value={id}
          onChange={(e) => setId(e.target.value)}
          placeholder="Например, 1"
        />
        <button>Открыть →</button>
      </form>
    </>
  );
}
