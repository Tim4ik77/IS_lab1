import { ticketFields } from '../ticketFields';

export default function TicketSortControls({ sortField, sortDirection, onSortChange, onDirectionChange }) {
  return (
    <>
      <label>
        Сортировка
        <select value={sortField} onChange={(event) => onSortChange(event.target.value)}>
          {Object.entries(ticketFields).map(([fieldName, label]) => (
            <option key={fieldName} value={fieldName}>{label}</option>
          ))}
        </select>
      </label>
      <button type="button" onClick={() => onDirectionChange(sortDirection === 'asc' ? 'desc' : 'asc')}
        aria-label="Изменить направление сортировки">
        {sortDirection === 'asc' ? '↑ По возрастанию' : '↓ По убыванию'}
      </button>
    </>
  );
}
