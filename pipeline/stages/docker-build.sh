echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

(docker pull ${IMAGE_NAME_CACHE_BUILDER} || true) & (docker pull ${IMAGE_NAME_CACHE_FINAL} || true) & wait

docker build --target=builder_image --cache-from ${IMAGE_NAME_CACHE_BUILDER} -t ${IMAGE_NAME_CACHE_BUILDER} -f docker/Dockerfile-k8s_dev-travis_build .
docker build --cache-from ${IMAGE_NAME_CACHE_BUILDER} --cache-from ${IMAGE_NAME_CACHE_FINAL} -t ${IMAGE_NAME_CACHE_FINAL} -t ${IMAGE_NAME_TAG} -f docker/Dockerfile-k8s_dev-travis_build .

docker push ${IMAGE_NAME_CACHE_BUILDER}
docker push ${IMAGE_NAME_CACHE_FINAL}
docker push ${IMAGE_NAME_TAG}