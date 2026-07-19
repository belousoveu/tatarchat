package ru.tatarchat.tatarchat.services;

public interface StorageService {
    /**
     * Загружает файл в облачное хранилище и возвращает публичную ссылку.
     * @param fileName имя файла
     * @param content байтовое содержимое файла
     * @return публичная ссылка на файл
     */
    String uploadFile(String fileName, byte[] content);

    /**
     * Удаляет файл по его идентификатору или ссылке.
     * @param fileIdentifier идентификатор файла в системе провайдера
     */
    void deleteFile(String fileIdentifier);
}