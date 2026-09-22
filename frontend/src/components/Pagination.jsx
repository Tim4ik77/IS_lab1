export default function Pagination({ page, pageSize, total, onPageChange, onPageSizeChange }) {
  const totalCount = Number(total);
  const pageCount = Math.max(1, Math.ceil(totalCount / pageSize));

  return (
    <div className="pagination">
      <span>
        {totalCount
          ? `${page * pageSize + 1}–${Math.min((page + 1) * pageSize, totalCount)} из ${total}`
          : '0 билетов'}
      </span>
      <div>
        <label>
          На странице{' '}
          <select value={pageSize} onChange={(event) => onPageSizeChange(Number(event.target.value))}>
            {[10, 20, 50].map((size) => <option key={size}>{size}</option>)}
          </select>
        </label>
        <button disabled={page === 0} onClick={() => onPageChange(page - 1)}>←</button>
        <span>{page + 1} / {pageCount}</span>
        <button disabled={(page + 1) * pageSize >= totalCount}
          onClick={() => onPageChange(page + 1)}>→</button>
      </div>
    </div>
  );
}
