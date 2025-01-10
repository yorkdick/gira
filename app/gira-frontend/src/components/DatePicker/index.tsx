import React from 'react';
import { DatePicker as AntDatePicker } from 'antd';
import type { DatePickerProps } from 'antd';
import dayjs from 'dayjs';
import { formatDate, formatRelativeTime } from '@/utils/date';
import styles from './style.module.less';

interface CustomDatePickerProps extends Omit<DatePickerProps, 'value' | 'onChange'> {
  value?: string;
  onChange?: (value: string | null) => void;
  showRelativeTime?: boolean;
}

const DatePicker: React.FC<CustomDatePickerProps> = ({
  value,
  onChange,
  showRelativeTime = true,
  ...props
}) => {
  const handleChange = (date: dayjs.Dayjs | null) => {
    onChange?.(date ? formatDate(date.toDate()) : null);
  };

  return (
    <AntDatePicker
      {...props}
      className={styles.datePicker}
      value={value ? dayjs(value) : null}
      onChange={handleChange}
      format={(value) => {
        if (!value) return '';
        const date = formatDate(value.toDate());
        return showRelativeTime ? formatRelativeTime(date) : date;
      }}
      allowClear
      placeholder="选择日期"
      showToday
      showTime={false}
      placement="bottomLeft"
      getPopupContainer={(trigger) => trigger.parentElement || document.body}
      renderExtraFooter={() => (
        <div className={styles.footer}>
          <span
            className={styles.quickSelect}
            onClick={() => handleChange(dayjs())}
          >
            今天
          </span>
          <span
            className={styles.quickSelect}
            onClick={() => handleChange(dayjs().add(1, 'day'))}
          >
            明天
          </span>
          <span
            className={styles.quickSelect}
            onClick={() => handleChange(dayjs().add(7, 'day'))}
          >
            一周后
          </span>
        </div>
      )}
    />
  );
};

export default DatePicker; 