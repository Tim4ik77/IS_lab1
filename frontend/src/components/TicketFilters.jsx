import { useState } from 'react';
import { labels } from '../schema';
import { ticketFields } from '../ticketFields';

export default function TicketFilters({ filters, onApply, onRemove, onClear, children }) {
  const [fieldName, setFieldName] = useState('name');
  const [filterValue, setFilterValue] = useState('');

  function applyFilter(event) {
    event.preventDefault();
    onApply(fieldName, filterValue);
  }

  return (
    <>
      <form className="toolbar" onSubmit={applyFilter}>
        <label>
          Поле
          <select value={fieldName} onChange={(event) => {
            setFieldName(event.target.value);
            setFilterValue('');
          }}>
            {Object.entries(ticketFields)
              .filter(([name]) => name !== 'id')
              .map(([name, label]) => <option key={name} value={name}>{label}</option>)}
          </select>
        </label>
        <label>
          Точное совпадение
          {fieldName === 'type' ? (
            <select value={filterValue} onChange={(event) => setFilterValue(event.target.value)} required>
              <option value="">Выберите тип</option>
              {['VIP', 'USUAL', 'CHEAP'].map((ticketType) => (
                <option value={ticketType} key={ticketType}>{labels[ticketType]}</option>
              ))}
            </select>
          ) : (
            <input placeholder="Введите полное значение" value={filterValue}
              onChange={(event) => setFilterValue(event.target.value)} />
          )}
        </label>
        <button>Применить</button>
        {children}
      </form>
      {Object.keys(filters).length > 0 && (
        <div className="toolbar">
          {Object.entries(filters).map(([name, value]) => (
            <button key={name} onClick={() => onRemove(name)}>
              {ticketFields[name]}: {value || '(пусто)'} ×
            </button>
          ))}
          <button onClick={onClear}>Сбросить всё</button>
        </div>
      )}
    </>
  );
}
