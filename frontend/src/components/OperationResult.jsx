import { useState } from 'react';
import TicketTable from './TicketTable';
import Details from './Details';

export default function OperationResult({ result, onEdit, onDelete }) {
  const [page, setPage] = useState(0);

  if (result === null) {
    return null;
  }

  let content;
  if (result.count !== undefined) {
    content = <p>Количество билетов: {result.count}</p>;
  } else if (Array.isArray(result)) {
    const pages = Math.max(1, Math.ceil(result.length / 10));
    const currentPage = Math.min(page, pages - 1);
    content = (
      <>
        <TicketTable
          items={result.slice(currentPage * 10, (currentPage + 1) * 10)}
          onEdit={onEdit}
          onDelete={onDelete}
        />
        <div className="pagination">
          <span>
            Всего: {result.length}. Страница {currentPage + 1} из {pages}
          </span>
          <button disabled={currentPage === 0} onClick={() => setPage(currentPage - 1)}>
            Назад
          </button>
          <button disabled={currentPage + 1 >= pages} onClick={() => setPage(currentPage + 1)}>
            Вперёд
          </button>
        </div>
      </>
    );
  } else {
    content = (
      <>
        <p>
          <a href={`#/tickets/${result.id}`}>Открыть билет #{result.id}</a>
        </p>
        <Details value={result} />
      </>
    );
  }

  return (
    <section className="operation-result">
      <h2>Результат</h2>
      {content}
    </section>
  );
}
