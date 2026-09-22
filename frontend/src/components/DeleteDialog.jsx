import { useEffect, useState } from 'react';
import { api } from '../api';
import { refLabel } from '../schema';
import Modal from './Modal';
import Field from './Field';
import ErrorMessage from './ErrorMessage';

export default function DeleteDialog({ kind, item, references, onClose, onDeleted }) {
  const [links, setLinks] = useState(kind === 'tickets' ? 0 : null);
  const [replacement, setReplacement] = useState('');
  const [error, setError] = useState(null);
  const [busy, setBusy] = useState(false);
  const base = kind === 'tickets' ? '/tickets' : `/references/${kind}`;
  useEffect(() => {
    let active = true;
    if (kind !== 'tickets')
      api(`${base}/${item.id}/links`)
        .then((r) => active && setLinks(r.count))
        .catch((e) => active && setError(e));
    return () => {
      active = false;
    };
  }, [base, item.id, kind]);
  async function remove(e) {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      await api(`${base}/${item.id}${replacement ? `?replacementId=${replacement}` : ''}`, {
        method: 'DELETE',
      });
      onDeleted();
    } catch (e) {
      setError(e);
      if (kind !== 'tickets')
        api(`${base}/${item.id}/links`)
          .then((r) => setLinks(r.count))
          .catch(() => {});
    } finally {
      setBusy(false);
    }
  }
  return (
    <Modal title={`Удалить объект #${item.id}?`} onClose={() => !busy && onClose()}>
      <form onSubmit={remove}>
        <p>«{item.name || refLabel(kind, item)}» будет удалён из системы.</p>
        <ErrorMessage error={error} />
        {links === null ? (
          <p>Проверяем связи…</p>
        ) : Number(links) > 0 ? (
          <>
            <p>
              Связанных объектов: <strong>{links}</strong>. Выберите объект, на который нужно
              перенести ссылки.
            </p>
            <Field
              field={{
                key: 'replacement',
                label: 'Заменить на',
                kind: 'ref',
                source: kind,
                required: true,
              }}
              value={replacement}
              onChange={setReplacement}
              references={{
                [kind]: references[kind].filter((r) => String(r.id) !== String(item.id)),
              }}
            />
          </>
        ) : (
          <p className="muted">Другие объекты удалены не будут.</p>
        )}
        <div className="actions">
          <button type="button" onClick={onClose} disabled={busy}>
            Отмена
          </button>
          <button
            className="danger"
            disabled={busy || links === null || (Number(links) > 0 && !replacement)}
          >
            {busy ? 'Удаление…' : 'Удалить'}
          </button>
        </div>
      </form>
    </Modal>
  );
}
